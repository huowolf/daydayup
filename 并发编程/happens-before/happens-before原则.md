# Happens-before规则

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

```java
flag = true; //2
x = 42; //1
```

所以，线程 A 将 flag = true 写⼊**但没有为** **x** **重新赋值时**，线程 B 可能就已经打印了 x 是 0

那么为 flag 加上 volatile 关键字试⼀下:

```java
volatile boolean flag = false;
```

即便加上了 *volatile* 关键字，这个问题在 *java1.5* 之前还是没有解决，但 *java1.5* 和其之后的版本对 *volatile* 语义做了增强 ，问题得以解决，这就离不开 Happens-before 规则的约束了，总共有 6 个规则。

## 程序顺序性规则

**⼀个线程中**的每个操作, happens-before 于该线程中的任意后续操作。注意这⾥是⼀个线程中的操作，其实隐含了「as-if-serial」语义: 说⽩了就是只要执⾏结果不被改变，⽆论怎么"排序"，都是对的。

这个规则是⼀个基础规则，happens-before 是多线程的规则，所以要和其他规则约束在⼀起才能体现出它的顺序性，别着急，继续向下看。

## Volatile变量规则

对⼀个 volatile 域的写, happens-before 于任意后续对这个 volatile 域的读。

```java
public class RecorderExample {
    private int x = 0;
    private int y = 1;
    private volatile boolean flag = false;

    public void writer(){
        x = 42;		//1
        y = 50;		//2
        flag = true;	//3
    }

    public void reader(){
        if (flag) {		//4
            System.out.println("x:" + x);		//5
            System.out.println("y:" + y);		//6
        }
    }
}
```

这⾥涉及到了 volatile 的内存增强语义，先来看个表格:

| 能否重排序  | 第二个操作 | 第二个操作  | 第二个操作  |
| :---------- | ---------- | ----------- | ----------- |
| 第一个操作  | 普通读/写  | volatile 读 | volatile 写 |
| 普通读/写   | -          | -           | NO          |
| volatile 读 | NO         | NO          | NO          |
| volatile 写 | -          | NO          | NO          |

从这个表格 **最后⼀列** 可以看出:

如果第⼆个操作为 volatile 写，不管第⼀个操作是什么，都不能重排序，**这就确保了** **volatile** **写之前的操作不会被重排序到volatile写之后**， 拿上⾯的代码来说，代码 1 和 2 不会被重排序到代码 3 的后⾯，但代码 1 和2 可能被重排序 (没有依赖也不会影响到执⾏结果)，说到这⾥和 **程序顺序性规则**是不是就已经关联起来了呢？

从这个表格的 **倒数第⼆⾏** 可以看出:

如果第⼀个操作为 volatile 读，不管第⼆个操作是什么，都不能重排序，**这确保了volatile读之后的操作不会被重排序到volatile读之前 ** ，拿上⾯的代码来说，代码 4 是读取 volatile 变量，代码 5 和 6 不会被重排序到代码 4 之前。

### 传递性规则

如果 A happens-before B, 且 B happens-before C, 那么 A happens-before C。

* x =42 和 y = 50 Happens-before flag = true , 这是**规则** **1**
* 写变量(代码 3) flag=true Happens-before 读变量(代码 4) if(flag) ，这是**规则** **2**

根据**规则** **3**传递性规则， x =42 Happens-before 读变量 if(flag)

如果线程 B 读到了 flag 是 true，那么 x =42 和 y = 50 对线程 B 就⼀定可⻅了，这就是Java1.5 的增强。

### 监视器锁规则

对⼀个锁的解锁 happens-before 于随后对这个锁的加锁。

```java
public class SynchronizedExample {
    private int x = 0;

    public void synBlock(){
        // 1.加锁
        synchronized (SynchronizedExample.class){
            x = 1; // 2.对x赋值
        }
        // 3. 解锁
    }
}
```

先获取锁的线程，对 x 赋值之后释放锁，另外⼀个再获取锁，⼀定能看到对 x 赋值的改动

### start()规则

如果线程 A 执⾏操作 ThreadB.start() (启动线程B), 那么 A 线程的 ThreadB.start() 操作 happens-before 于线程 B中的任意操作，也就是说，主线程 A 启动子线程 B 后，⼦线程 B 能看到主线程在启动⼦线程 B 前的操作。

```java
public class StartExample {
    private int x = 0;
    private int y = 1;
    private boolean flag = false;

    public static void main(String[] args) {
        StartExample startExample = new StartExample();

        Thread t1 = new Thread(startExample::writer,"线程1");
        startExample.x = 10;
        startExample.y = 20;
        startExample.flag = true;

        t1.start();
        System.out.println("主线程结束");
    }

    private void writer() {
        System.out.println("x:" + x);
        System.out.println("y:" + y);
        System.out.println("flag:" + flag);
    }
}
```

## join()规则

如果线程 A 执⾏操作 ThreadB.join() 并成功返回, 那么线程 B 中的任意操作 happens-before 于线程 A 从ThreadB.join() 操作成功返回，**和** **start** **规则刚好相反**，主线程 A 等待⼦线程 B 完成，当⼦线程 B 完成后，主线程能够看到⼦线程 B 的赋值操作。

```java
public class JoinExample {
    private int x = 0;
    private int y = 1;
    private boolean flag = false;

    public static void main(String[] args) throws InterruptedException {
        JoinExample joinExample = new JoinExample();
        
        Thread t1 = new Thread(joinExample::writer,"线程1");
        t1.start();

        t1.join();

        System.out.println("x:" + joinExample.x);
        System.out.println("y:" + joinExample.y);
        System.out.println("flag:" + joinExample.flag);
        System.out.println("主线程结束");
    }

    private void writer() {
        this.x = 100;
        this.y = 200;
        this.flag = true;
    }
}
```

## 总结

1、**Happens-before** **重点是解决前⼀个操作结果对后⼀个操作可⻅**，这些规则解决了多线程编程的可⻅性与有序性问题，但还没有完全解决原⼦性问题(除了synchronized)。

2、start 和 join 规则也是解决主线程与⼦线程通信的⽅式之⼀。

3、从内存语义的⻆度来说, volatile 的 写-读 与锁的 释放-获取 有相同的内存效果；volatile 写和锁的释放有相同的内存语义; volatile 读与锁的获取有相同的内存语义。 

volatile 解决的是可⻅性问题，synchronized 解决的是原⼦性问题，这绝对不是⼀回事

