// Paso 1
// Columnas según OpenFlights: airline, airline_id, src_airport, src_airport_id, dst_airport, dst_airport_id, codeshare, stops, equipment
val routesSchema = StructType(
  Array(
    StructField("airline", StringType, true),
    StructField("airline_id", IntegerType, true),
    StructField("src_airport", StringType, true),
    StructField("src_airport_id", IntegerType, true),
    StructField("dst_airport", StringType, true),
    StructField("dst_airport_id", IntegerType, true),
    StructField("codeshare", StringType, true),
    StructField("stops", IntegerType, true),
    StructField("equipment", StringType, true)
  )
)

// Usamos \\N para representar los valores nulos, común en este dataset
val routesDF = spark.read
    .option("nullValue", "\\N")
    .schema(routesSchema)
    .csv("hdfs://hdfs-traffic:8020/tmp/routes.dat")

// Paso 2
routesDF.write
    .format("mongodb")
    .option(
      "spark.mongodb.write.connection.uri",
      "mongodb://mongo-traffic:27017/practica.routes"
    )
    .mode("overwrite")
    .save()

// Paso 3
val routesVerify = spark.read
    .format("mongodb")
    .option(
      "spark.mongodb.read.connection.uri",
      "mongodb://mongo-traffic:27017/practica.routes"
    )
    .load()

routesVerify.show(5)
