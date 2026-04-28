package com.pq.zlbackjava.service.impl;

import com.pq.zlbackjava.entity.User;
import com.pq.zlbackjava.mapper.UserMapper;
import com.pq.zlbackjava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        return userMapper.findByUsernameAndPassword(username, password);
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        }
        // 插入新用户
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean verifySecurityQuestion(String username, String question, String answer) {
        User user = userMapper.findSecurityQuestionByUsername(username);
        if (user == null) {
            return false;
        }
        // 验证安全问题和答案
        return user.getQuestion().equals(question) && user.getAnswer().equals(answer);
    }

    @Override
    public boolean resetPassword(String username, String newPassword) {
        return userMapper.updatePassword(username, newPassword) > 0;
    }

    @Override
    public User getSecurityQuestion(String username) {
        User user = userMapper.findSecurityQuestionByUsername(username);
        if (user != null) {
            // 不返回答案，只返回问题
            user.setAnswer(null);
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.findById(id);
    }
}

