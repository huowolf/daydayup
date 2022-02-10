# 深入了解Volatile

## happens-before之volatile变量规则

| 能否重排序  | 第二个操作 | 第二个操作  | 第二个操作  |
| :---------- | ---------- | ----------- | ----------- |
| 第一个操作  | 普通读/写  | volatile 读 | volatile 写 |
| 普通读/写   | -          | -           | NO          |
| volatile 读 | NO         | NO          | NO          |
| volatile 写 | -          | NO          | NO          |

上⾯的表格是 JMM 针对编译器定制的 volatile 重排序的规则，那 JMM 是怎样禁⽌重排序的呢？答案是**内存屏障**

## 内存屏障

为了实现 volatile 的内存语义，编译器在⽣成字节码时，会在指令序列中插⼊内存屏障来禁⽌特定类型的处理器重排序。

JMM 就将内存屏障插⼊策略分为 4 种:

1. 在每个 volatile 写操作的前⾯插⼊⼀个 StoreStore 屏障

2. 在每个 volatile 写操作的后⾯插⼊⼀个 StoreLoad 屏障

3. 在每个 volatile 读操作的后⾯插⼊⼀个 LoadLoad 屏障

4. 在每个 volatile 读操作的后⾯插⼊⼀个 LoadStore 屏障

1 和 2 ⽤图形描述：

![image-20220210172914339](https://gitee.com/huowolf/pic-md/raw/master/image-20220210172914339.png)

3 和 4 ⽤图形描述：

![image-20220210173057242](https://gitee.com/huowolf/pic-md/raw/master/image-20220210173057242.png)

## volatile 写-读的内存语义

1、线程 A 写⼀个volatile变量, 实质上是线程 A 向接下来将要读这个 volatile 变量的某个线程发出了(其对共享变量所做修改的)消息。

2、线程 B 读⼀个 volatile 变量,实质上是线程 B 接收了之前某个线程发出的(在写这个 volatile 变量之前对共享变量所做修改的)消息。

3、线程 A 写⼀个 volatile 变量, 随后线程 B 读这个 volatile 变量, 这个过程实质上是线程 A 通过主内存向线程B 发送消息。