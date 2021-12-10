package com.example.springboot.caffeine.service.impl;

import com.example.springboot.caffeine.pojo.UserInfo;
import com.example.springboot.caffeine.service.UserInfoService;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
public class UserServiceImpl implements UserInfoService {

    /**
     * 模拟数据库存储数据
     */
    private HashMap<Integer, UserInfo> userInfoMap = new HashMap<>();

    @Autowired
    Cache<String,Object> caffeineCache;

    @Override
    public void addUserInfo(UserInfo userInfo) {
        userInfoMap.put(userInfo.getId(),userInfo);
        //加入缓存
        caffeineCache.put(String.valueOf(userInfo.getId()),userInfo);
    }

    @Override
    public UserInfo getById(Integer id) {
        log.info("getById");
        //先读取缓存
        UserInfo userInfo = (UserInfo) caffeineCache.getIfPresent(String.valueOf(id));
        if(userInfo != null){
            log.info("从缓存中查找到用户信息，id：{}",id);
            return userInfo;
        }

        //如果缓存不存在，则从数据库中查找
        log.info("从数据库中查找到用户信息，id：{}",id);
        userInfo = userInfoMap.get(id);
        //如果数据不为空，则加入缓存
        if(userInfo != null){
            caffeineCache.put(String.valueOf(userInfo.getId()),userInfo);
        }
        return userInfo;
    }

    @Override
    public void updateUserInfo(UserInfo userInfo) {
        //更新数据库中的记录
        userInfoMap.put(userInfo.getId(), userInfo);
        //删除缓存
        caffeineCache.invalidate(String.valueOf(userInfo.getId()));
    }

    @Override
    public void deleteById(Integer id) {
        //删除数据库中的记录
        userInfoMap.remove(id);
        //删除缓存
        caffeineCache.invalidate(String.valueOf(id));
    }
}
