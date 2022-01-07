package com.demo1;

public class demo3 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (true) {
                // 响应中断
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("青秧线程被中断，程序退出。");
                    return;
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    System.out.println("青秧线程休眠被中断，程序退出。");
                }
            }
        });
        thread.start();

        Thread.sleep(2000);
        thread.interrupt();
    }
}
