import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用Lock锁对临界资源num的操作上锁。
 * 不管哪个线程拿到锁，只有符合条件的才能打印。
 * 该方法的缺点：
 * 导致很多无效的锁竞争，即线程拿到了锁，但是啥也没干，就释放了锁。
 *
 * //?处
 * 我觉得就是，在干完自己的任务后，就去递增循环变量count。
 */
public class Demo3 {

    private int num;
    private Lock lock = new ReentrantLock();

    private void printABC(int threadId){
        int count = 0;
        while(count < 10) {
            lock.lock();
            try {
                if(num % 3 == threadId){
                    System.out.print((char)('A'+ num%3) + " ");
                    num++;

                    count++;//?
                }
            }finally {
                lock.unlock();
            }
        }

    }


    public static void main(String[] args) {
        Demo3 demo = new Demo3();

        new Thread(() -> {
            demo.printABC(0);
        }).start();

        new Thread(() -> {
            demo.printABC(1);
        }).start();

        new Thread(() -> {
            demo.printABC(2);
        }).start();
    }
}
