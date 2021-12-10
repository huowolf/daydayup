## 中断相关三个方法

* interrupt()

  用于线程中断，该方法并不能直接中断线程，只会将线程的中断标志位改为true。它只会给线程发送一个中断状态，线程是否中断取决于线程内部对该中断信号做什么响应，若不处理该中断信号，线程就不会中断。

  **简而言之，就是通知线程你需要执行中断了，具体对该中断的影响需要依赖当前线程对中断的处理。**（本人理解）

* interrupted() 与 isInterrupted() 傻傻分不清？

  直接上源码。

  ```java
  public static boolean interrupted() {
      return currentThread().isInterrupted(true);
  }
  ```

  ```java
  public boolean isInterrupted() {
      return isInterrupted(false);
  }
  ```

  interrupted() 和 isInterrupted() 方法都调用了isInterrupted(false)的有参构造方法。来，看下这个有参构造方法：

  ```java
  private native boolean isInterrupted(boolean ClearInterrupted);
  ```

  看下这个方法的源码注释。

  > Tests if some Thread has been interrupted. The interrupted state is reset or not based on the value of ClearInterrupted that is passed.

  这个方法返回线程是否被中断了，中断状态是否被清楚依赖于ClearInterrupted变量的值。

  so，interrupted() 与 isInterrupted() 的区别：

  * interrupted() ：判断线程是否处于中断状态，并重置中断状态。
  * isInterrupted()：判断线程是否处于中断状态，不会重置中断状态。

## 聊一下如何中断线程？

* 原生的中断通知与中断检测

  上代码。

  ```java
      public static void main(String[] args) {
          Thread thread = new Thread(() -> {
              while (true) {
                  Thread.yield();
  
                  // 响应中断
                  if (Thread.currentThread().isInterrupted()) {
                      System.out.println("青秧线程被中断，程序退出。");
                      return;
                  }
              }
          });
          thread.start();
          thread.interrupt();
      }
  ```

* 条件变量 ，更优雅的方式

  ```java
  public class InterruptThread extends Thread{
      public volatile boolean running = true;  //重点:volatile
  
      public static void main(String[] args) throws InterruptedException {
          InterruptThread interruptThread = new InterruptThread();
          interruptThread.start();
          Thread.sleep(10);
          interruptThread.running = false;
      }
  
      public void run(){
          int n = 0;
          while (running) {
              n++;
              System.out.println(n + " hello!");
          }
          System.out.println("end!");
      }
  }
  ```

## 中断处理与中断传播

```java
public class MyThread extends Thread {
    @Override
    public void run() {
        super.run();
        try{
            for (int i = 0; i < 500000; i++) {
                if (this.interrupted()) {
                    System.out.println("should be stopped and exit");
                    throw new InterruptedException();
                }
                System.out.println("i=" + (i + 1));
            }
            System.out.println("this line cannot be executed. cause thread throws exception");
        }catch(InterruptedException e){
            /**这样处理不好
             * System.out.println("catch interrupted exception");
             * e.printStackTrace();
             */
             Thread.currentThread().interrupt();//这样处理比较好
        }
    }
}
```

## Sleep与中断

```java
private static void test3() throws InterruptedException {
	Thread thread = new Thread(() -> {
		while (true) {
			// 响应中断
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("Java技术栈线程被中断，程序退出。");
				return;
			}

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				System.out.println("Java技术栈线程休眠被中断，程序退出。");
			}
		}
	});
	thread.start();
	Thread.sleep(2000);
	thread.interrupt();
}
```

该示例会中断失败，因为sleep会清除中断标记。

```java
private static void test4() throws InterruptedException {
	Thread thread = new Thread(() -> {
		while (true) {
			// 响应中断
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("Java技术栈线程被中断，程序退出。");
				return;
			}

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				System.out.println("Java技术栈线程休眠被中断，程序退出。");
				Thread.currentThread().interrupt();
			}
		}
	});
	thread.start();
	Thread.sleep(2000);
	thread.interrupt();
}
```

对比该代码，解决方案就是传播中断。

## 参考

1、https://www.cnblogs.com/hapjin/p/5450779.html

2、https://zhuanlan.zhihu.com/p/149205707

3、https://www.cnblogs.com/wulianshang/p/5801902.html