package com.example.springboot.caffeine.controller;

import com.example.springboot.caffeine.pojo.UserInfo;
import com.example.springboot.caffeine.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("{id}")
    public Object getUserInfo(@PathVariable Integer id) {
        UserInfo userInfo = userInfoService.getById(id);
        if (userInfo == null) {
            return "没有该用户";
        }
        return userInfo;
    }

    @PostMapping("/")
    public Object createUserInfo(@RequestBody UserInfo userInfo) {
        userInfoService.addUserInfo(userInfo);
        return "SUCCESS";
    }

    @PutMapping("/")
    public Object updateUserInfo(@RequestBody UserInfo userInfo) {
        userInfoService.updateUserInfo(userInfo);
        return "SUCCESS";
    }

    @DeleteMapping("{id}")
    public Object deleteUserInfo(@PathVariable Integer id) {
        userInfoService.deleteById(id);
        return "SUCCESS";
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

}
