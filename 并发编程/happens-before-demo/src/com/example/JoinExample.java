package com.example;

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
