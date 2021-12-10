package com.huowolf.thread;

/**
 * 通过变量和方法传递数据
 */
public class MyThread2 implements Runnable {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void run() {
        System.out.println("hello " + name);
    }

    public static void main(String[] args) {
        MyThread2 myThread = new MyThread2();
        myThread.setName("world");
        Thread thread = new Thread(myThread);
        thread.start();
    }
}