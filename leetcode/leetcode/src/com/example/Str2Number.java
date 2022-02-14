package com.example;

/**
 * 输入一个字符串，转换成数字，
 * 输入的字符串都是合法的数字形式，包含负号和小数点。
 */
public class Str2Number {

    public static void main(String[] args) {
	    double result1 = str2Number("220");
        System.out.println(result1);
        double result2 = str2Number("-220");
        System.out.println(result2);
        double result3 = str2Number("220.22");
        System.out.println(result3);
        double result4 = str2Number("-220.22");
        System.out.println(result4);
    }

    public static double str2Number(String str){
        double result = 0;
        boolean isfushu = false;
        if(str.charAt(0) == '-'){
            isfushu = true;
        }
        int i = str.indexOf(".");
        if(i == -1){
            i = str.length();
        }

        //整数部分
        if(isfushu){
            for (int j = i-1; j > 0; j--) {
                result += (str.charAt(j)-'0')*Math.pow(10,i-1-j);
            }
        }else{
            for (int j = i-1; j >= 0; j--) {
                result += (str.charAt(j)-'0')*Math.pow(10,i-1-j);
            }
        }

        //小数部分
        for (int j = i+1; j < str.length(); j++) {
            result += (str.charAt(j)-'0')*Math.pow(0.1,(j-i));
        }

        if (isfushu){
            return -result;
        }else{
            return result;
        }
    }
}
