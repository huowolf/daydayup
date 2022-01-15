package com.example.controller;

import com.example.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @GetMapping("/test")
    public UserInfo userInfo(@RequestParam Long id, @RequestHeader(required = false) String token){
        log.info("id:{},token:{}",id,token);

        //测试异常时是否会触发服务降级
        //int i = 1/0;
        UserInfo userInfo = new UserInfo("test","man",28);
        return userInfo;
    }
}
