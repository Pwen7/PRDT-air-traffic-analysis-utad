// Paso 1
val countriesRDD = sc.textFile("hdfs://hdfs-traffic:8020/tmp/countries.txt")

// Paso 2
val transformedRDD = countriesRDD.map { line =>
  val parts = line.split(" ## ")
  // Divide por "::" y tomando el segundo elemento
  val name = parts(0).split("::")(1)
  val iso_code = parts(1).split("::")(1)
  val dafif_code = parts(2).split("::")(1)
  (name, iso_code, dafif_code) // Devuelve tupla
}

import spark.implicits._ // Conversiones implícitas -> usar .toDF()
// Define las columnas
val columns = Seq("name", "iso_code", "dafif_code")

// Convierte el RDD de tuplas a un DataFrame especificando columnas
val countriesDF = transformedRDD.toDF(columns: _*)
countriesDF.printSchema()
countriesDF.show(3, false) // false, no truncar columnas largas

// Paso 3
countriesDF.write
  .option("header", "true")
  .mode("overwrite")
  .csv("hdfs://hdfs-traffic:8020/practica/countries")

// Paso 4
val countriesVerify = spark.read
  .option("header", "true")
  .option("inferSchema", "true")
  .csv("hdfs://hdfs-traffic:8020/practica/countries")
countriesVerify.show(5, false)

