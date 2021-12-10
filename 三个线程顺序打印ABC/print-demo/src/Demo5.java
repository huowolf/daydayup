import java.util.concurrent.Semaphore;

/**
 * Semaphore
 * 用来控制同时访问某个特定资源的操作数量
 * 内部维护了一个计数器，其值为可以访问的共享资源的个数。
 *
 * 这个方法很巧妙，我觉得需要好好理解理解。
 * 让三个线程依次获得信号量，这样就实现了顺序打印
 */
public class Demo5 {

    private static Semaphore s1 = new Semaphore(1);
    private static Semaphore s2 = new Semaphore(0);
    private static Semaphore s3 = new Semaphore(0);

    public void printABC(Semaphore currentThread,Semaphore nextThread,char printChar){
        for (int i = 0; i < 10; i++) {
            try{
                //如果线程的信号量大于等于1，是可以获取到信号量的，否则线程阻塞
                currentThread.acquire(); //当前线程计数器减1，访问共享资源
                System.out.print(printChar+" ");

                nextThread.release();  //下一个线程计数器加1，相当于让下一个线程获得执行权
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Demo5 demo = new Demo5();
        new Thread(() -> {
            demo.printABC(s1,s2,'A');
        }).start();
        new Thread(() -> {
            demo.printABC(s2,s3,'B');
        }).start();
        new Thread(() -> {
            demo.printABC(s3,s1,'C');
        }).start();
    }
}
