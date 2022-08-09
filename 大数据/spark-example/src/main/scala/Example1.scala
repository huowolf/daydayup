import org.apache.spark.sql.SparkSession

/**
 * 给定一组键值对(“spark”, 2)、(“hadoop”, 6）、(“hadoop”, 4）、(“spark”, 6），
 * key表示图书名称，value表示某天图书销量，计算每种图书的每天的平均销量。
 */
object Example1 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()
    val rdd = spark.sparkContext.parallelize(Array(("spark",2),("hadoop",6),("hadoop",4),("spark",6)))

    //(spark,2)
    rdd.foreach(println)

    //mapValues(x => (x,1)): 将数据转化为(x,1),x代表数据本身，1代表次数
    //(spark,(2,1))
    val mapRdd = rdd.mapValues(x => (x, 1))
    mapRdd.foreach(println)

    //reduceByKey((x,y) => (x._1+y._1,x._2+y._2)): 将数据分组聚合，将聚合结果相加
    //(spark,(8,2))
    mapRdd.reduceByKey((x,y) => (x._1+y._1,x._2+y._2)).foreach(println)

    val avgCount = rdd.mapValues(x => (x,1)).reduceByKey((x,y) => (x._1+y._1,x._2+y._2)).mapValues(x => (x._1 / x._2)).collect()
    avgCount.foreach(println)
  }
}
