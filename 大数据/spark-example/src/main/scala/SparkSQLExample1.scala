import org.apache.spark.sql.SparkSession
//idea 添加provided依赖到类路径

object SparkSQLExample1 {
  case class Person(name: String, age: Long)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("Spark SQL basic example")
      //.config("spark.some.config.option", "some-value")
      .getOrCreate()

    //runBasicDataFrameExample(spark)

    runDatasetCreationExample(spark)

    spark.stop()
  }

  def runBasicDataFrameExample(spark: SparkSession): Unit = {
    val df = spark.read.json("input/people.json")

    // Displays the content of the DataFrame to stdout
    df.show()

    import spark.implicits._
    // Print the schema in a tree format
    df.printSchema()

    // Select only the "name" column
    df.select("name").show()

    // Select everybody, but increment the age by 1
    df.select($"name", $"age" + 1).show()

    // Select people older than 21
    df.filter($"age" > 21).show()

    // Count people by age
    df.groupBy("age").count().show()

    // Register the DataFrame as a SQL temporary view
    df.createOrReplaceTempView("people")

    val sqlDF = spark.sql("SELECT * FROM people")
    sqlDF.show()

    // Register the DataFrame as a global temporary view
    df.createGlobalTempView("people")

    // Global temporary view is tied to a system preserved database `global_temp`
    spark.sql("SELECT * FROM global_temp.people").show()

    // Global temporary view is cross-session
    spark.newSession().sql("SELECT * FROM global_temp.people").show()
  }

  def runDatasetCreationExample(spark: SparkSession) = {

    import spark.implicits._
    // Encoders are created for case classes
    val caseClassDS = Seq(Person("Andy", 32)).toDS()
    caseClassDS.show()

    // Encoders for most common types are automatically provided by importing spark.implicits._
    val primitiveDS = Seq(1, 2, 3).toDS()
    primitiveDS.map(_ + 1).collect() // Returns: Array(2, 3, 4)

    // DataFrames can be converted to a Dataset by providing a class. Mapping will be done by name
    val path = "input/people.json"
    val peopleDS = spark.read.json(path).as[Person]
    peopleDS.show()
  }
}
