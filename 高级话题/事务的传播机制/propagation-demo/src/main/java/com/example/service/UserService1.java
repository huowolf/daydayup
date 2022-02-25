package com.example.service;

import com.example.entity.AccessLog;
import com.example.entity.User;
import com.example.mapper.AccessLogMapper;
import com.example.mapper.UserMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService1 {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AccessLogMapper accessLogMapper;

    //@Autowired
    //private UserService userService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void testMain(){
        User user = User.builder().name("zhangsan").age(22).build();
        userMapper.insert(user);

        //userService.testB();
        ((UserService1)AopContext.currentProxy()).testB();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void testB() {
        AccessLog accessLog = AccessLog.builder().siteId(10L).count(100).build();
        accessLogMapper.insert(accessLog);

        int i = 1/0;

        AccessLog accessLog2 = AccessLog.builder().siteId(20L).count(200).build();
        accessLogMapper.insert(accessLog2);
    }
}
