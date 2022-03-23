package com.example;

/**
 * 有一对兔子，从出生后第 3 个月起每个月都生一对兔子，小兔子长到第三个月后每个月
 * 又生一对兔子，假如兔子都不死，问每个月的兔子总数为多少？
 */
public class RabbitCount {

    public static void main(String[] args) {
        int rabbitCount = RabbitCount(10);
        System.out.println(rabbitCount+"对");
    }

    public static int RabbitCount(int n){
        if(n==1 || n==2){
            return 1;
        }else{
            return RabbitCount(n-1)+RabbitCount(n-2);
        }
    }
}
