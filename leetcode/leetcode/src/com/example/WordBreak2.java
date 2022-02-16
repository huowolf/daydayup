package com.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 给你一个字符串 s 和一个字符串列表 wordDict 作为字典。请你判断是否可以利用字典中出现的单词拼接出 s 。
 * 注意：不要求字典中出现的单词全部都使用，并且字典中的单词可以重复使用。
 *
 * 使用DFS算法 + 记忆化搜索
 */
public class WordBreak2 {

    public static void main(String[] args) {
        WordBreak2 wordBreak = new WordBreak2();
        System.out.println(wordBreak.wordBreak("leetcode", Arrays.asList("leet","code")));
        System.out.println(wordBreak.wordBreak("applepenapple", Arrays.asList("apple","pen")));
        System.out.println(wordBreak.wordBreak("catsandog", Arrays.asList("cats","dog","sand","and","cat")));
    }

    public boolean wordBreak(String s, List<String> wordDict) {
        HashSet<String> hashSet = new HashSet<>(wordDict);
        HashMap<Integer,Boolean> memo  = new HashMap<>();
        return canBreak(0,s,hashSet,memo);
    }

    /**
     * 该函数代表从start开始的子串能否被拆分
     */
    public boolean canBreak(int start, String s, HashSet<String> wordSet,HashMap<Integer,Boolean> memo){
        if(start == s.length()){ //指针越界，s一步步成功划分为单词，才能走到越界这步，现在没有剩余子串
            return true;
        }

        //返回记忆化结果
        if(memo.containsKey(start)){
            return memo.get(start);
        }

        for (int i = start+1; i <= s.length(); i++) {
            String prefix = s.substring(start,i);
            if(wordSet.contains(prefix) && canBreak(i,s,wordSet,memo)){
                memo.put(start,true);
                return true;
            }
        }
        return false;
    }
}
