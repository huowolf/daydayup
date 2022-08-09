import org.apache.spark.sql.SparkSession


/**
 * RDD转换为DataFrame
 */
object RDDToDF {
  case class User(name:String, age:Int)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("RDDToDF")
      .getOrCreate()

    import spark.implicits._
    val sc = spark.sparkContext

    val idRDD = sc.textFile("input/id.txt")
    idRDD.toDF("id").show()


    //通过case class将RDD转换为DF
    sc.makeRDD(List(("zhangsan",30), ("lisi",40))).map(t=>User(t._1, t._2)).toDF.show
  }
}
