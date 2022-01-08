package com.example.springboot.caffeine.service;

import com.example.springboot.caffeine.pojo.UserInfo;

public interface UserInfoService {

    /**
     * 增加用户信息
     *
     * @param userInfo 用户信息
     */
    void addUserInfo(UserInfo userInfo);

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserInfo getById(Integer id);

    /**
     * 修改用户信息
     *
     * @param userInfo 用户信息
     * @return 用户信息
     */
    void updateUserInfo(UserInfo userInfo);

    /**
     * 删除用户信息
     *
     * @param id 用户ID
     */
    void deleteById(Integer id);

}
