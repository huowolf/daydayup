package com.huowolf.thread;

import java.util.concurrent.*;

public class ThreadPoolDemo {

    public static void main(String[] args) {
        //创建线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        //提交MyCallable的任务去执行
        Future<String> future = executorService.submit(new MyCallable());
        if (!future.isDone()) {
            System.out.println("task has not finished.");
        }
        try {
            System.out.println("task return:" + future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            //关闭线程池
            executorService.shutdown();
        }
    }
}
