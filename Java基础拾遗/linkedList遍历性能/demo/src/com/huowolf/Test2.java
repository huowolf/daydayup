package com.huowolf;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * 遍历链表，效率最高的方式，顺序遍历每个元素。
 */
public class Test2 {
    public static void main(String[] args) {
        Random random = new Random();

        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            list.add(random.nextInt(1000));
        }

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()){
            Integer tmp = iterator.next();
            System.out.println(tmp);
        }
    }
}
