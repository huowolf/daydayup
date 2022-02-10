package com.example;

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
