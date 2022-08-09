import org.apache.spark.sql.SparkSession

/**
 * DF转换为RDD
 */
object DFToRDD {
  case class User(name:String, age:Int)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("DFToRDD")
      .getOrCreate()

    import spark.implicits._
    val sc = spark.sparkContext

    val df = sc.makeRDD(List(("zhangsan",30), ("lisi",40))).map(t=>User(t._1, t._2)).toDF
    val rdd = df.rdd
    val array = rdd.collect
    var name = array(0).getAs[String]("name")
    println(name)
  }
}
