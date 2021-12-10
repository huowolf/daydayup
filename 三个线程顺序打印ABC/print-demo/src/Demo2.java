/**
 * 三个线程顺序打印ABC
 * join
 * 在A线程中调用了B线程的join()方法时，表示只有当B线程执行完毕时，A线程才能继续执行。
 *
 * 注意：这种方法只能实现三个线程共同顺序打印一次ABC,无法实现循环顺序打印ABC
 * 如果在run方法中增加for循环10次，打印结果是10个A，10个B，10个C,即如AAABBBCCC格式。
 *
 * 协作型
 */
public class Demo2 {


    static class PrintABC implements Runnable {
        private Thread beforeThread;
        private char printChar;

        public PrintABC(Thread beforeThread,char printChar){
            this.beforeThread = beforeThread;
            this.printChar = printChar;
        }

        @Override
        public void run() {
            if(beforeThread != null){
                try {
                    beforeThread.join();
                    System.out.print(printChar+" ");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.print(printChar+" ");
            }
        }

//        public void run() {
//            for (int i = 0; i < 10; i++) {
//                if(beforeThread != null){
//                    try {
//                        beforeThread.join();
//                        System.out.print(printChar+" ");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }else{
//                    System.out.print(printChar+" ");
//                }
//            }
//        }

    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new PrintABC(null,'A'));
        Thread t2 = new Thread(new PrintABC(t1,'B'));
        Thread t3 = new Thread(new PrintABC(t2,'C'));
        t1.start();
        t2.start();
        t3.start();
    }
}
