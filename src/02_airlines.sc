// Paso 1
// Imports necesarios para operaciones posteriores
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

// Columnas según OpenFlights: airline_id, name, alias, iata, icao, callsign, country, active
val airlinesSchema = StructType(
  Array(
    StructField("airline_id", IntegerType, nullable = true),
    StructField("name", StringType, nullable = true),
    StructField("alias", StringType, nullable = true),
    StructField("iata", StringType, nullable = true),
    StructField("icao", StringType, nullable = true),
    StructField("callsign", StringType, nullable = true),
    StructField("country", StringType, nullable = true),
    StructField("active", StringType, nullable = true) // String inicialmente
  )
)

// Leer
val airlinesRaw = spark.read
    .schema(airlinesSchema)
    .csv("hdfs://hdfs-traffic:8020/tmp/airlines.dat")

// Paso 2
val airlinesDF = airlinesRaw.withColumn(
  "active",
  when(upper(col("active")) === "Y", true)
      .otherwise(false)
      .cast(BooleanType)
)

airlinesDF.printSchema()

// Paso 3
val countriesJoin =
    countriesDF.select(col("name").alias("country_name"), col("iso_code"))

// Left join: airlines.country == countries.name
// Añade iso_code como country_iso y elimina columna country_name
val airlinesEnriched = airlinesDF
    .join(
      countriesJoin,
      airlinesDF("country") === countriesJoin("country_name"),
      "left"
    )
    .withColumnRenamed("iso_code", "country_iso")
    .drop("country_name")

// Paso 4
airlinesEnriched.write
    .partitionBy("country")
    .mode("overwrite")
    .parquet("hdfs://hdfs-traffic:8020/practica/airlines/")

// Paso 5
val airlinesVerify = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .parquet("hdfs://hdfs-traffic:8020/practica/airlines")

airlinesVerify.show(5, false)
