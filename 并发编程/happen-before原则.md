## Happens-before规则

程序猿基于happens-before规则提供的内存可见性来编程，只要遵循这个规则就不会出问题。

Happens-before规则主要用来约束两个操作，两个操作之间具有happens-before关系，并不意味着前一个操作要在后一个操作之前执行，**happens-before仅仅要求前一个操作（执行的结果）对后一个操作可见**。

```java
class ReorderExample {
 int x = 0;
 boolean flag = false;
 public void writer() {
 	x = 42; //1
 	flag = true; //2
 }
 public void reader() {
 if (flag) { //3
 	System.out.println(x); //4
 	}	
 }
}
```

假设 A 线程执⾏ writer ⽅法，B 线程执⾏ reader ⽅法，打印出来的 x 可能会是 0，这是因为代码 1和 2 没有**数据依赖**关系，所以可能被重排序。