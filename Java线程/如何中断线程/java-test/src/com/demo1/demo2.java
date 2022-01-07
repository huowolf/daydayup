package com.demo1;

public class demo2 {

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            while (true) {
                Thread.yield();

                // 响应中断
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("青秧线程被中断，程序退出。");
                    return;
                }
            }
        });
        thread.start();
        thread.interrupt();
    }

}
