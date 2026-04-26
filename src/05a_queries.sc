import org.apache.spark.sql.functions._

// Airports: leído desde PostgreSQL via JDBC
val jdbcProps = new java.util.Properties()
jdbcProps.setProperty("user", "postgres")
jdbcProps.setProperty("password", "postgres")
jdbcProps.setProperty("driver", "org.postgresql.Driver")

val airportsDF = spark.read.jdbc(
  "jdbc:postgresql://postgres-traffic:5432/practica",
  "airports",
  jdbcProps
)

// Airlines: leído desde HDFS en formato Parquet
val airlinesDF = spark.read
    .parquet("hdfs://hdfs-traffic:8020/practica/airlines/")

// Routes: leído desde MongoDB
val routesDF = spark.read
    .format("mongodb")
    .option(
      "spark.mongodb.read.connection.uri",
      "mongodb://mongo-traffic:27017/practica.routes"
    )
    .load()

airportsDF.createOrReplaceTempView("airports")
airlinesDF.createOrReplaceTempView("airlines")
routesDF.createOrReplaceTempView("routes")

// Consulta 1
// DataFrame API
airportsDF
    .filter(col("altitude").isNotNull)
    .orderBy(desc("altitude"))
    .select("name", "city", "country", "altitude")
    .limit(1)
    .show(false)

// SparkSQL
spark
    .sql("""
    SELECT name, city, country, altitude
    FROM airports
    WHERE altitude IS NOT NULL
    ORDER BY altitude DESC
    LIMIT 1
""").show(false)

// Consulta 2
// DataFrame API
val numSpainAirports = airportsDF
    .filter(col("country") === "Spain")
    .count()

// SparkSQL
spark
    .sql("""
    SELECT count(*) as numSpainAirports
    FROM airports
    WHERE country = 'Spain'
""").show()

// Consulta 3
// DataFrame API
airportsDF
    .filter(col("dst") === "E")
    .select("country")
    .distinct()
    .orderBy("country")
    .show(false)

// SparkSQL
spark
    .sql("""
    SELECT DISTINCT country
    FROM airports
    WHERE dst = 'E'
    ORDER BY country
""").show(false)

// Consulta 4
// DataFrame API
val numEEUUAirlines = airlinesDF
    .filter(col("country") === "United States")
    .count()

// SparkSQL
spark
    .sql("""
    SELECT count(*) as numEEUUAirlines
    FROM airlines
    WHERE country = 'United States'
""").show()

// Consulta 5
// DataFrame API
airlinesDF
    .filter(col("active") === false)
    .groupBy("country")
    .count()
    .orderBy(desc("count"))
    .limit(10)
    .show()

// SparkSQL
spark
    .sql("""
    SELECT country, count(*) as num_inactive
    FROM airlines
    WHERE active = false
    GROUP BY country
    ORDER BY num_inactive DESC
    LIMIT 10
""").show()

// Consulta 6
// DataFrame API
val activeCountryAirlines = airlinesDF
    .filter(col("active") === true)
    .select("country")
    .distinct()

val last80CountryAirports = airportsDF
    .filter(col("latitude") > 80)
    .select("country")
    .distinct()

val query6 = activeCountryAirlines
    .join(
      lat80CountryAirports,
      activeCountryAirlines("country") === last80CountryAirports("country"),
      "inner" // Condición del join
    )
    .select(activeCountryAirlines("country"))
    .show(false)

// SparkSQL
spark
    .sql("""
    SELECT DISTINCT al.country
    FROM airlines al
    INNER JOIN airports ap ON al.country = ap.country
    WHERE al.active = true AND ap.latitude > 80
""").show(false)
