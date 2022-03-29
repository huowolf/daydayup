package com.example;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

public class SumRecursiveTask extends RecursiveTask<Long> {
    private long[] numbers;
    private int start;
    private int end;

    public SumRecursiveTask(long[] numbers) {
        this.numbers = numbers;
        this.start = 0;
        this.end = numbers.length;
    }

    public SumRecursiveTask(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        if (length < 20000) {  //小于20000个就不在进行拆分
            return sum();
        }
        SumRecursiveTask leftTask = new SumRecursiveTask(numbers, start, start + length / 2); //进行任务拆分
        SumRecursiveTask rightTask = new SumRecursiveTask(numbers, start + (length / 2), end); //进行任务拆分
        leftTask.fork(); //把该子任务交友ForkJoinPoll线程池去执行
        rightTask.fork(); //把该子任务交友ForkJoinPoll线程池去执行
        return leftTask.join() + rightTask.join(); //把子任务的结果相加
    }


    private long sum() {
        int sum = 0;
        for (int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }


    public static void main(String[] args) {
        long[] numbers = LongStream.rangeClosed(1, 100000000).toArray();

        Long result = new ForkJoinPool().invoke(new SumRecursiveTask(numbers));
        System.out.println("result：" +result);
    }
}
