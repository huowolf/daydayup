import org.apache.spark.sql.SparkSession

/**
 * 创建DataSet
 */
object CreateDS {

  case class Person(name: String, age: Long)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("CreateDS")
      .getOrCreate()

    import spark.implicits._
    val sc = spark.sparkContext

    //通过样例类创建DS
    val caseClassDS = Seq(Person("zhangsan",2)).toDS()
    caseClassDS.show()

    //通过基本类型的序列创建DS
    val ds = Seq(1,2,3,4,5).toDS
    ds.show()
  }
}
