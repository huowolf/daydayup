package com.huowolf.thread;

/**
 * 通过回调函数传递数据
 *
 *
 *
 * 回调主要强调的是依赖于第三方程序的执行，
 * 即外部程序执行有结果之后，才会回调通知线程有值了，而线程拿到这些值才能往下去执行
 */
class Data {
    public int value = 0;
}

class Work {
    public void process(Data data, Integer... numbers) {
        for (int n : numbers) {
            data.value += n;
        }
    }
}

public class MyThread3 extends Thread {
    private Work work;

    public MyThread3(Work work) {
        this.work = work;
    }

    public void run() {
        java.util.Random random = new java.util.Random();
        Data data = new Data();
        int n1 = random.nextInt(1000);
        int n2 = random.nextInt(2000);
        int n3 = random.nextInt(3000);
        work.process(data, n1, n2, n3);   // 使用回调函数

        /**
         * 这里我的理解：data.value的值依赖于一个外部函数work.process的执行结果
         */
        System.out.println(String.valueOf(n1) + "+" + String.valueOf(n2) + "+"
                + String.valueOf(n3) + "=" + data.value);
    }

    public static void main(String[] args) {
        Thread thread = new MyThread3(new Work());
        thread.start();
    }
}
