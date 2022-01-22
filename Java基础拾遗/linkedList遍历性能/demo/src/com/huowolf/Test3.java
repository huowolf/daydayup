package com.huowolf;

import java.util.LinkedList;
import java.util.Random;

/**
 * 查看out文件夹下的Test3.class
 * 可以知道：foreach的实质就是iterator
 */
public class Test3 {

    public static void main(String[] args) {
        Random random = new Random();

        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            list.add(random.nextInt(1000));
        }

        for (Integer tmp : list) {
            System.out.println(tmp);
        }
    }
}
