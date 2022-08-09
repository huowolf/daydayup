package mysql

import org.apache.spark.sql.SparkSession

object MysqlQueryDemo {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("MysqlQueryDemo")
      .master("local")
      .getOrCreate()

    val jdbcDF = spark.read
      .format("jdbc")
      .option("url", "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai")
      .option("dbtable", "test_user")
      .option("user", "root")
      .option("password", "root")
      .load()
    jdbcDF.show()
  }
}
