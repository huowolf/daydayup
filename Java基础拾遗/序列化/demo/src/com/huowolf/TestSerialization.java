package com.huowolf;

import java.io.Serializable;

/**
 * 定义一个需要被序列化的类，
 * 该类自行完成序列化与反序列化逻辑。
 *
 * 1、实现Serializable接口
 * 2、复写writeObject与readObject方法
 */
public class TestSerialization implements Serializable {

    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException{
        s.defaultWriteObject();
        s.writeObject(num);
        System.out.println("writeObject of "+ this.getClass().getName());
    }

    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        num = (Integer)s.readObject();
        System.out.println("readObject of "+ this.getClass().getName());
    }
}
