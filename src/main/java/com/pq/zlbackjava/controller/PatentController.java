package com.pq.zlbackjava.controller;

import com.pq.zlbackjava.dto.ApiResponse;
import com.pq.zlbackjava.dto.PatentClassifyRequest;
import com.pq.zlbackjava.service.PatentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 专利控制器
 */
@RestController
@RequestMapping("/api/patent")
@CrossOrigin(origins = "*")
public class PatentController {

    @Autowired
    private PatentService patentService;

    /**
     * 专利分类预测
     */
    @PostMapping("/classify")
    public ApiResponse<Object> classify(@RequestBody PatentClassifyRequest request,
                                        @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (request.getSummary() == null || request.getSummary().trim().isEmpty()) {
            return ApiResponse.error(400, "专利摘要不能为空");
        }

        int userId = 0;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try {
                String[] parts = token.split("_");
                if (parts.length >= 2) {
                    userId = Integer.parseInt(parts[1]);
                }
            } catch (Exception e) {
                // token格式无效，userId保持0
            }
        }

        Object result = patentService.classify(request, userId);
        
        // 如果result是Map类型，检查code字段
        if (result instanceof java.util.Map) {
            java.util.Map<String, Object> resultMap = (java.util.Map<String, Object>) result;
            Integer code = (Integer) resultMap.get("code");
            String message = (String) resultMap.get("message");
            Object data = resultMap.get("data");
            
            if (code != null && code == 200) {
                return ApiResponse.success(message, data);
            } else {
                return ApiResponse.error(code != null ? code : 500, message != null ? message : "预测失败");
            }
        }
        
        return ApiResponse.success("预测成功", result);
    }
}

