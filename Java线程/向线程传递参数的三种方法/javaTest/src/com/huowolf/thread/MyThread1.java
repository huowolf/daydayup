package com.huowolf.thread;

/**
 * 通过构造函数传递数据
 */
public class MyThread1 extends Thread {
    private String name;

    public MyThread1(String name) {
        this.name = name;
    }

    public void run() {
        System.out.println("hello " + name);
    }

    public static void main(String[] args) {
        Thread thread = new MyThread1("world");
        thread.start();
    }
}
