import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * 解析嵌套Json
 * https://blog.csdn.net/qq_21439395/article/details/80710180
 */
object DoJsonExample {
  def main(args: Array[String]): Unit = {
    val session = SparkSession.builder()
      .master("local[*]")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()

    // 导入隐式转换和functions
    import session.implicits._
    import org.apache.spark.sql.types._
    import org.apache.spark.sql.functions._

    val opds = session.createDataset(
      // 三引号中，编写json字符串
      List("""{"name":"xx","address":{"city":"bj"}}""")
    )
    val otherPeople = session.read.json(opds)
    otherPeople.printSchema()

    //读取普通json文件
    val json1 = session.read.json("input/jsonlog1.json")
    json1.printSchema()

    //读取嵌套json文件
    val json = session.read.json("input/jsonlog2.json")
    json.printSchema()
    //操作嵌套json

    //DSL 语法的查询
    json.select("address.province").show()

    // 使用sql语法查询
    json.createTempView("v_tmp")
    session.sql("select address.city from  v_tmp").show()

    //操作Json嵌套数组
    val json3 = session.read.json("input/jsonlog3.json")
    json3.printSchema()
    json3.show()

    // 利用explode函数  把json数组进行展开， 数组中的每一条数据，都是一条记录
    val explodeDF = json3.select($"name", explode($"myScore")).toDF("name", "score")
    explodeDF.printSchema()
    explodeDF.show()

    // 再次进行查询  类似于普通的数据库表  默认schema： score1, 可以通过as 指定schema名称
    val json3Res = explodeDF.select($"name", $"score.subject", $"score.score" as "score")
    // 创建临时视图
    json3Res.createTempView("v_jsonArray")
    // 写sql，求平均值
    session.sql("select name,avg(score) from v_jsonArray group by name").show()


    val json4 = Seq(
      (0, """{"device_id": 0, "device_type": "sensor-ipad", "ip": "68.161.225.1", "cn": "United States"}"""))
      .toDF("id", "json")
    json4.printSchema()

    // 利用get_json_object 从 json字符串中，提取列
    val jsDF = json4.select($"id",
      get_json_object($"json", "$.device_type").alias("device_type"),
      get_json_object($"json", "$.ip").alias("ip"),
      get_json_object($"json", "$.cn").alias("cn"))
    jsDF.printSchema()


  }
}
