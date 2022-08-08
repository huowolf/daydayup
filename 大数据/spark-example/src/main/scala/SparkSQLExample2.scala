import org.apache.spark.sql.SparkSession
//idea 添加provided依赖到类路径

object SparkSQLExample2 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("Spark SQL basic example")
      //.config("spark.some.config.option", "some-value")
      .getOrCreate()

    val df = spark.read.json("input/student.json")

    //show方法是展示所有的数据，也可以show(int rownums) 展示前N条数据
    df.show(5)

    //查询某些列的数据
    df.select("id","age").show()

    //组合使用DataSet对象的select()，where()，orderBy()方法查找id大于3的同学的id，姓名，年龄以及电话，按id的降序排列
    import org.apache.spark.sql.functions._
    df.select("id","name","age","phone").where("id > 3").orderBy(desc("id")).show()

    //注册临时表/视窗，进行sql语句操作
    df.createOrReplaceTempView("student")
    //spark.sql("select id,name from student").show()
    spark.sql("select id,name from student where id > 3 order by id desc").show()

    //使用groupBy()方法进而学生年龄分布
    df.groupBy("age").count().show()

    spark.stop()
  }

}
