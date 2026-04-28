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
    public ApiResponse<Object> classify(@RequestBody PatentClassifyRequest request) {
        if (request.getSummary() == null || request.getSummary().trim().isEmpty()) {
            return ApiResponse.error(400, "专利摘要不能为空");
        }
        
        Object result = patentService.classify(request);
        
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

