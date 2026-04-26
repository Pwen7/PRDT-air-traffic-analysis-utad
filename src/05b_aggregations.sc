import org.apache.spark.sql.functions._

// Paso 1
val routesFiltered = spark.sql("""
    SELECT *
    FROM routes
    WHERE stops = 0
""")
routesFiltered.createOrReplaceTempView("routesFiltered")

val airportsFiltered = spark.sql("""
    SELECT *
    FROM airports
    WHERE altitude > 200
""")
airportsFiltered.createOrReplaceTempView("airportsFiltered")

val airlinesFiltered = spark.sql("""
    SELECT *
    FROM airlines
    WHERE active = false
""")
airlinesFiltered.createOrReplaceTempView("airlinesFiltered")

// Paso 2
val finalDF = spark
    .sql("""
    SELECT al.country, count(*) as num_routes
    FROM routesFiltered r
    INNER JOIN airlinesFiltered al ON r.airline_id = al.airline_id
    INNER JOIN airportsFiltered ap ON r.dst_airport_id = ap.airport_id
    GROUP BY al.country
    ORDER BY num_routes DESC
""")

// Paso 3
finalDF.write
    .mode("overwrite")
    .parquet("hdfs://hdfs-traffic:8020/practica/aggregations/")

// Paso 4
val aggVerify = spark.read
    .parquet("hdfs://hdfs-traffic:8020/practica/aggregations/")

aggVerify.show()
