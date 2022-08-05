import org.apache.spark.{SparkConf, SparkContext}


//命令行提交
//./bin/spark-submit --class MySpark --master spark://localhost:7077 /home/hadoop/anaconda2/MaSpark/out/artifacts/MaSpark_jar/MaSpark.jar
object MySpark {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("mySpark")

    //本机的spark就用local，远端的就写ip
    // 如果是打成jar包运行则需要去掉 setMaster("local")因为在参数中会指定。
    //conf.setMaster("local")

    conf.setMaster("spark://192.168.93.131:7077")
    conf.setJars(Array[String]("E:\\my-code\\spark-example\\target\\spark-example-1.0-SNAPSHOT-jar-with-dependencies.jar"))
    val sc =new SparkContext(conf)

    val rdd =sc.parallelize(List(1,2,3,4,5,6)).map(_*3)
    val mappedRDD=rdd.filter(_>10).collect() //对集合求和
    println(rdd.reduce(_+_)) //输出大于10的元素
    for(arg <- mappedRDD)
      print(arg+" ")
    println()
    println("math is work")
  }
}