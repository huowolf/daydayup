package com.huowolf;

/**
 * String str=“abc” : 在字符串常量池中创建
 * JVM 首先会检查该对象是否在字符串常量池中，如果在，就返回该对象引用，否则新的字符串将在常量池中被创建。
 *
 * String str2= new String("abc")：在常量池和堆中分别创建一个
 * 首先在编译类文件时，"abc"常量字符串将会放入到常量结构中，在类加载时，“abc"将会在常量池中创建；
 * 其次，在调用 new 时，JVM 命令将会调用 String 的构造函数，同时引用常量池中的"abc” 字符串，在堆内存中创建一个 String 对象；
 * 最后，str 将引用 String 对象。
 *
 * 在字符串常量中，默认会将对象放入常量池；
 * 在字符串变量中，对象是会创建在堆内存中，同时也会在常量池中创建一个字符串对象，
 * String 对象中的 char 数组将会引用常量池中的 char 数组，并返回堆内存对象引用。
 *
 * 在调用 intern 方法之后，会去常量池中查找是否有等于该字符串对象的引用，有就返回引用。
 * 在 JDK1.7 版本以后，由于常量池已经合并到了堆中，所以不会再复制具体字符串了，只是会把首次遇到的字符串的引用添加到常量池中；
 */
public class Main {

    public static void main(String[] args) {
        String str1= "abc";
        String str2= new String("abc");
        String str3= str2.intern();

        //false
        //常量池地址 不等于 堆地址
        System.out.println(str1==str2);

        //false
        //堆地址 不等于 常量池地址
        System.out.println(str2==str3);

        //true
        //常量池地址 等于 常量池地址
        System.out.println(str1==str3);
    }
}
