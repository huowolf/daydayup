package com.huowolf;

public class Thing implements Cloneable{

    public Thing(){
        System.out.println("构造函数被执行了。。。。");
    }

    @Override
    protected Thing clone() {
        try {
            return (Thing) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
