package com.example;

import java.io.File;

/**
 * 遍历文件夹及文件夹下的所有文件
 */
public class VisitAllFile {
    public static void main(String[] args) {
        VisitAllFile1(new File("E:\\学习记录\\daydayup\\leetcode\\leetcode"));
        System.out.println("===================");
        VisitAllFile2(new File("E:\\学习记录\\daydayup\\leetcode\\leetcode"));
    }

    public static void VisitAllFile1(File file){
        System.out.println(file);
        if(file.isDirectory()){
            File[] childrenFile = file.listFiles();
            for (File child : childrenFile) {
                VisitAllFile1(child);
            }
        }
    }

    public static void VisitAllFile2(File file){
        System.out.println(file);
        if(file.isDirectory()){
            String[] children = file.list();
            for (String child : children) {
                VisitAllFile2(new File(file,child));
            }
        }
    }
}
