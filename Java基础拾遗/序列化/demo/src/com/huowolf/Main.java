package com.huowolf;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
	    TestSerialization testSerialization = new TestSerialization();
        testSerialization.setNum(10);
        System.out.println("序列化之前的值："+ testSerialization.getNum());

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("test.txt"));
        oos.writeObject(testSerialization);

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("test.txt"));
        testSerialization = (TestSerialization)ois.readObject();
        System.out.println("序列化的值："+ testSerialization.getNum());
    }
}
