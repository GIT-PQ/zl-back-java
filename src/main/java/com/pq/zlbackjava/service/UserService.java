package com.pq.zlbackjava.service;

import com.pq.zlbackjava.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);

    /**
     * 用户注册
     * @param user 用户信息
     * @return 是否成功
     */
    boolean register(User user);

    /**
     * 找回密码 - 验证安全问题
     * @param username 用户名
     * @param question 安全问题
     * @param answer 答案
     * @return 是否验证通过
     */
    boolean verifySecurityQuestion(String username, String question, String answer);

    /**
     * 重置密码
     * @param username 用户名
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetPassword(String username, String newPassword);

    /**
     * 根据用户名获取安全问题
     * @param username 用户名
     * @return 用户信息（包含question）
     */
    User getSecurityQuestion(String username);

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Integer id);
}

