package com.pq.zlbackjava.entity;

import lombok.Data;

/**
 * 用户实体类
 */
@Data
public class User {
    /**
     * 用户id
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 安全问题，用于找回密码
     */
    private String question;

    /**
     * 安全问题的答案，用于找回密码
     */
    private String answer;
}

