package com.huowolf;

public class Main {

    public static void main(String[] args) {
	    Thing thing = new Thing();

        //这里不会执行构造函数
        Thing cloneThing = thing.clone();
    }
}
