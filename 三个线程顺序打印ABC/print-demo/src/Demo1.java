/**
 * 三个线程顺序打印ABC
 * 方法一：synchronized + wait/notify
 * 1、定义一个共享变量num,让这三个线程去修改这个共享变量
 * 2、因为是要操作共享变量，所以上来先上锁（synchronized）
 * 3、根据num%3的值，将打印任务指派到对应的线程
 * 4、如果线程拿到锁，但是此时不是自己的任务，那么要执行wait，即进入阻塞队列，释放当前锁。
 * 5、如果线程拿到锁，且是自己的任务，那么执行打印操作，修改num的值，最后唤醒其他线程。
 *
 * 这是竞争型
 */
public class Demo1 {

    private int num = 0;
    private static final Object lock = new Object();

    void printABC(int target) {
        for (int i = 0; i < 10; i++) {
            synchronized (lock) {
                //不是自己的任务
                while (num % 3 != target){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //是自己的任务
                System.out.print((char)('A'+ num%3) + " ");
                num++;
                lock.notifyAll();
            }
        }
    }


    public static void main(String[] args) {
        Demo1 demo = new Demo1();

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
