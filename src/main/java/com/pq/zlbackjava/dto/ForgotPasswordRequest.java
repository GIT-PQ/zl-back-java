package com.pq.zlbackjava.dto;

import lombok.Data;

/**
 * 找回密码请求DTO
 */
@Data
public class ForgotPasswordRequest {
    private String username;
    private String question;
    private String answer;
    private String newPassword;
}

