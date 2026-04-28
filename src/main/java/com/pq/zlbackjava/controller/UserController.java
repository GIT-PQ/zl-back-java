package com.pq.zlbackjava.controller;

import com.pq.zlbackjava.dto.ApiResponse;
import com.pq.zlbackjava.dto.ForgotPasswordRequest;
import com.pq.zlbackjava.dto.LoginRequest;
import com.pq.zlbackjava.dto.RegisterRequest;
import com.pq.zlbackjava.entity.User;
import com.pq.zlbackjava.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<Object> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        if (user != null) {
            // 生成简单的token（实际项目中应使用JWT）
            String token = "token_" + user.getId() + "_" + System.currentTimeMillis();
            // 返回token和用户信息
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("token", token);
            data.put("username", user.getUsername());
            data.put("id", user.getId());
            return ApiResponse.success("登录成功", data);
        } else {
            return ApiResponse.error(401, "用户名或密码错误");
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<Object> register(@RequestBody RegisterRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        
        boolean success = userService.register(user);
        if (success) {
            // 重新查询用户以获取生成的ID
            User registeredUser = userService.getSecurityQuestion(request.getUsername());
            long userId = registeredUser != null ? registeredUser.getId() : System.currentTimeMillis();
            // 生成简单的token
            String token = "token_" + userId + "_" + System.currentTimeMillis();
            // 返回token和用户信息
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("token", token);
            data.put("username", user.getUsername());
            data.put("id", userId);
            return ApiResponse.success("注册成功", data);
        } else {
            return ApiResponse.error(400, "用户名已存在");
        }
    }

    /**
     * 验证安全问题（找回密码第一步）
     */
    @PostMapping("/verify-security-question")
    public ApiResponse<Object> verifySecurityQuestion(@RequestBody ForgotPasswordRequest request) {
        boolean verified = userService.verifySecurityQuestion(
            request.getUsername(),
            request.getQuestion(),
            request.getAnswer()
        );
        if (verified) {
            return ApiResponse.success("验证成功", null);
        } else {
            return ApiResponse.error(400, "安全问题或答案错误");
        }
    }

    /**
     * 重置密码（找回密码第二步）
     */
    @PostMapping("/reset-password")
    public ApiResponse<Object> resetPassword(@RequestBody ForgotPasswordRequest request) {
        // 先验证安全问题
        boolean verified = userService.verifySecurityQuestion(
            request.getUsername(),
            request.getQuestion(),
            request.getAnswer()
        );
        if (!verified) {
            return ApiResponse.error(400, "安全问题或答案错误");
        }
        
        // 重置密码
        boolean success = userService.resetPassword(request.getUsername(), request.getNewPassword());
        if (success) {
            return ApiResponse.success("密码重置成功", null);
        } else {
            return ApiResponse.error(500, "密码重置失败");
        }
    }

    /**
     * 获取用户的安全问题（用于找回密码页面显示）
     */
    @GetMapping("/security-question")
    public ApiResponse<Object> getSecurityQuestion(@RequestParam String username) {
        User user = userService.getSecurityQuestion(username);
        if (user != null) {
            return ApiResponse.success("获取成功", user.getQuestion());
        } else {
            return ApiResponse.error(404, "用户不存在");
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user")
    public ApiResponse<Object> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未授权");
        }
        
        // 从token中提取用户ID（简单实现，实际应使用JWT）
        String token = authorization.substring(7);
        try {
            // token格式: token_userId_timestamp
            String[] parts = token.split("_");
            if (parts.length >= 2) {
                Integer userId = Integer.parseInt(parts[1]);
                User user = userService.getUserById(userId);
                if (user != null) {
                    // 不返回密码和安全答案
                    user.setPassword(null);
                    user.setAnswer(null);
                    return ApiResponse.success("获取成功", user);
                }
            }
        } catch (Exception e) {
            // 忽略解析错误
        }
        return ApiResponse.error(401, "无效的token");
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ApiResponse<Object> logout() {
        // 简单实现，实际应该清除服务端的session或token
        return ApiResponse.success("退出成功", null);
    }
}

