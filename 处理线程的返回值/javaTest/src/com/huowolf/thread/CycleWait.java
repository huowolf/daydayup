package com.huowolf.thread;

/**
 * 使用join
 */
public class CycleWait implements Runnable {

    private String value;

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        value = "we have date now";
    }

    public static void main(String[] args) throws InterruptedException {
        CycleWait wait = new CycleWait();
        Thread thread = new Thread(wait);
        thread.start();
        //当值为null的时候一直循环，直到有值的时候才会返回。
        //少了这一步，则可能取出为空的值。
        thread.join();
        System.out.println(wait.value);
    }
}
