import org.apache.spark.{SparkConf, SparkContext}

/**
 * 文本包含很多行数据，每行数据由4个字段的值构成，不同值之间用逗号隔开，
 * 4个字段分别为orderid、userid、payment和productid，要求求出TopN个payment值。
 */
object TopN {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("TopN").setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")
    val lines = sc.textFile("input/TopN.txt")
    var num = 0 //编号
    lines
      .filter(line => line.trim.nonEmpty && (line.split(",").length == 4))
      .map(_.split(",")(2))
      .map(x => (x.toInt,""))
      .sortByKey(false)
      .map(x => x._1).take(5)  //top5
      .foreach(x =>{
        num += 1
        println(num + "\t" + x)
      })
  }
}
