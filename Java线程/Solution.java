package com.huowolf;

/*
A线程打印12345
B线程打印abcde
AB线程交叉打印，最终输入1a2b3c4d5e1a2b3c4d5e......
 */
public class Solution4 {

    public static void main(String[] args) {
        Object lock  = new Object();

        Thread t1 = new Thread(new PrintNumber(lock));
        Thread t2 = new Thread(new PrintChar(lock));
        t1.start();
        t2.start();
    }

    static class PrintNumber implements Runnable{
        Object lock;
        PrintNumber(Object lock){
            this.lock = lock;
        }
        @Override
        public void run() {
            synchronized (lock){
                while(true){
                    for (char c: "12345".toCharArray()) {
                        System.out.print(c+ " ");
                        lock.notify();
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    static class PrintChar implements Runnable{
        Object lock;
        PrintChar(Object lock){
            this.lock = lock;
        }
        @Override
        public void run() {
            synchronized (lock){
                while(true){
                    for (char c: "abcde".toCharArray()) {
                        System.out.print(c + " ");
                        lock.notify();
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
