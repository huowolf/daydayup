package mysql

import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object MysqlInsertDemo {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("MysqlInsertDemo")
      .master("local")
      .getOrCreate()

    val df = spark.read.option("header", "true").csv("input/USER_T.csv")
    df.show()
    val url = "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai"
    val prop = new Properties()
    prop.put("user", "root")
    prop.put("password", "root")
    df.write.mode(SaveMode.Append).jdbc(url, "test_user", prop)
  }
}
