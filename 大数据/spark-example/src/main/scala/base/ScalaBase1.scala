package base

import scala.collection.mutable

object ScalaBase1 {
  def main(args: Array[String]): Unit = {
    // 定义整型 List
    val list_init = List(11, 22, 33, 44)//创建列表
    var list =list_init:+666;//向列表的尾部
    var list0=66::list//向列表头部添加数据
    var list1=list0.updated(2,777)//修改下标为2的值
    for (i <- list1.indices) {//使用列表长度遍历
      print(list1.apply(i)+",")//逐一元素遍历输出
    }

    println()
    println(list1)//列表集合输出
    println(list1.max)//最大值
    println(list1.min)//最小值
    println(list1.sum)//计算总和
    println(list1.contains(66))//判断是否有某值

    // 定义 Set
    println("----set----")
    val set = mutable.Set(1,2,3,4,5)
    set.add(8);
    set+=10
    println("set.size:"+set.size);
    println("setInfo:"+set)

    // 定义 Map,key&value
    val map_info = Map("one" -> 1, "two" -> 2, "three" -> 3)
    var map=map_info + ("four"->4)
    println("-----map-----")
    println(map)
    println(map.get("two"))//根据key获取值
    println(map.contains("four"))//判断是否有four这个key

    // 创建两个不同类型元素的元组
    println("----元组----")
    val x = (10, "utest")
    println(x)
  }
}
