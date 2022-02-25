package com.example;

import com.example.service.UserService1;
import com.example.service.UserService2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService1 userService1;

    @Autowired
    private UserService2 userService2;

    @Test
    public void testMain1(){
        userService1.testMain();
    }

    @Test
    public void testMain2(){
        userService2.testMain();
    }
}
