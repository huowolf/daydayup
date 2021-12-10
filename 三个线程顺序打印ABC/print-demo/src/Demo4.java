import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock + Condition
 * 该思路类似于synchronzied+wait/notify
 * synchronzied对应于Lock
 * await/signal对应于wait/notify
 *
 * 同时可以创建多个Condition对象，实现精确唤醒下一个线程
 * 这样减少同步锁的无意义竞争，实现线程按照指定的次序执行。
 */
public class Demo4 {
    private int num;
    private static Lock lock = new ReentrantLock();
    private static Condition c1 = lock.newCondition();
    private static Condition c2 = lock.newCondition();
    private static Condition c3 = lock.newCondition();

    private void printABC(int threadId, Condition currentThread,Condition nextThread){
        int i = 0;
        while(i < 10){
            lock.lock();
            try{
                while (num % 3 != threadId){
                    currentThread.await();
                }

                System.out.print((char)('A'+ num%3) + " ");
                num++;
                i++;
                nextThread.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }


    public static void main(String[] args) {
        Demo4 demo = new Demo4();
        new Thread(()->{
            demo.printABC(0, c1, c2);
        }).start();

        new Thread(()->{
            demo.printABC(1, c2, c3);
        }).start();

        new Thread(()->{
            demo.printABC(2, c3, c1);
        }).start();
    }
}
