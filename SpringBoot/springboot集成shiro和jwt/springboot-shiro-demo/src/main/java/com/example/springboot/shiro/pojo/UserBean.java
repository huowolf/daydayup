package com.example.springboot.shiro.pojo;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class UserBean {
    private String username;

    private String password;

    private String role;

    private String permission;
}
