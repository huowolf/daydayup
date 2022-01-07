package com.demo1;


public class Run {

    public static void main(String[] args) {
        try {
            MyThread thread = new MyThread();
            thread.start();
            Thread.sleep(20);//modify 2000 to 20
            thread.interrupt();//请求中断MyThread线程
        } catch (InterruptedException e) {
            System.out.println("main catch");
            e.printStackTrace();
        }
        System.out.println("end!");
    }
}
