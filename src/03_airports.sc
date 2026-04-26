import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import java.util.Properties // Para las propiedades JDBC

// Paso 1
val feetToMeters = (feet: Int) => { feet * 0.3048 }
spark.udf.register("feetToMetersUDF", feetToMeters)

// Paso 2
// Columnas según OpenFlights: airport_id, name, city, country, iata, icao, latitude, longitude, altitude, timezone, dst, tz_database, type, source
val airportsSchema = StructType(
  Array(
    StructField("airport_id", IntegerType, true),
    StructField("name", StringType, true),
    StructField("city", StringType, true),
    StructField("country", StringType, true),
    StructField("iata", StringType, true),
    StructField("icao", StringType, true),
    StructField("latitude", DoubleType, true),
    StructField("longitude", DoubleType, true),
    StructField("altitude", DoubleType, true),
    StructField("timezone", StringType, true),
    StructField("dst", StringType, true),
    StructField("tz_db_timezone", StringType, true),
    StructField("type", StringType, true),
    StructField("source", StringType, true)
  )
)

// Leer
val airportsRaw = spark.read
    .schema(airportsSchema)
    .csv("hdfs://hdfs-traffic:8020/tmp/airports.dat")

// Paso 3
val airportsDF = airportsRaw.withColumn(
  "altitude",
  callUDF("feetToMetersUDF", col("altitude"))
)

// Paso 4
// Configuración JDBC para PostgreSQL
val jdbcProps = new java.util.Properties()
jdbcProps.setProperty("user", "postgres")
jdbcProps.setProperty("password", "postgres")
jdbcProps.setProperty("driver", "org.postgresql.Driver")

// Escribir en la tabla airports de PostgreSQL
airportsDF.write
    .mode("overwrite")
    .jdbc(
      "jdbc:postgresql://postgres-traffic:5432/practica",
      "airports",
      jdbcProps
    )

// Paso 5
val airportsVerify = spark.read
    .option("url", "jdbc:postgresql://postgres-traffic:5432/practica")
    .option("dbtable", "airports")
    .option("user", "postgres")
    .option("password", "postgres")
    .option("driver", "org.postgresql.Driver")
    .option("partitionColumn", "airport_id")
    .option("lowerBound", "1")
    .option("upperBound", "15000")
    .option("numPartitions", "6")
    .format("jdbc")
    .load()

airportsVerify.show(5)
