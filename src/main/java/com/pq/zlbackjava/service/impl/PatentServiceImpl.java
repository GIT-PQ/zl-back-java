package com.pq.zlbackjava.service.impl;

import com.pq.zlbackjava.dto.PatentClassifyRequest;
import com.pq.zlbackjava.service.PatentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 专利服务实现类
 */
@Service
public class PatentServiceImpl implements PatentService {

    @Value("${python.service.url:http://localhost:5000}")
    private String pythonServiceUrl;

    private final RestTemplate restTemplate;

    public PatentServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Object classify(PatentClassifyRequest request) {
        try {
            // 构建请求URL
            String url = pythonServiceUrl + "/predict";
            
            // 构建请求体
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("summary", request.getSummary());
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 创建请求实体
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送POST请求
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            // 返回响应数据
            return response.getBody();
            
        } catch (Exception e) {
            e.printStackTrace();
            // 返回错误响应
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "调用Python服务失败: " + e.getMessage());
            errorResponse.put("data", null);
            return errorResponse;
        }
    }
}

