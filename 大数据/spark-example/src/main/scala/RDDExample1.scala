import org.apache.spark.{SparkConf, SparkContext}

object RDDExample1 {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
      .setMaster("local")
      .setAppName("RDDExample1")

    val sc = new SparkContext(sparkConf)

    val input = sc.parallelize(List(1, 2, 3, 4))
    val result = input.map(x => x * x)
    println(result.collect().mkString(","))

    sc.stop()
  }
}
