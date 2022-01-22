package com.huowolf;

import java.util.LinkedList;
import java.util.Random;

/**
 * for循环遍历 性能最差。
 *
 * Node<E> node(int index)
 * 通过分前后半段来循环查找到对应的元素。
 * 但是通过这种方式来查询元素是非常低效的，
 * 特别是在 for 循环遍历的情况下，每一次循环都会去遍历半个 List。
 */
public class Test1 {

    public static void main(String[] args) {
        Random random = new Random();

        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            list.add(random.nextInt(1000));
        }

        for (int i = 0; i < 100; i++) {
            System.out.println(list.get(i));
        }
    }
}
