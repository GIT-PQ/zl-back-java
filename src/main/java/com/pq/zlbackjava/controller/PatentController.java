package com.pq.zlbackjava.controller;

import com.pq.zlbackjava.dto.ApiResponse;
import com.pq.zlbackjava.dto.ExcelPreviewResult;
import com.pq.zlbackjava.dto.PatentClassifyRequest;
import com.pq.zlbackjava.service.PatentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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

    /**
     * 文件预览接口
     */
    @PostMapping("/upload-preview")
    public ApiResponse<ExcelPreviewResult> uploadPreview(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error(400, "文件不能为空");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls") && !filename.endsWith(".csv"))) {
            return ApiResponse.error(400, "文件格式不支持，仅支持.xlsx/.xls/.csv");
        }

        try {
            ExcelPreviewResult result = patentService.parseExcel(file);
            return ApiResponse.success("文件解析成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "文件解析失败: " + e.getMessage());
        }
    }

    /**
     * 批量分类接口
     */
    @PostMapping("/batch-classify")
    public ApiResponse<Object> batchClassify(
            @RequestParam("file") MultipartFile file,
            @RequestParam("summaryColumn") String summaryColumn,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        if (file.isEmpty()) {
            return ApiResponse.error(400, "文件不能为空");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls") && !filename.endsWith(".csv"))) {
            return ApiResponse.error(400, "文件格式不支持，仅支持.xlsx/.xls/.csv");
        }

        if (summaryColumn == null || summaryColumn.trim().isEmpty()) {
            return ApiResponse.error(400, "摘要列名不能为空");
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

        Object result = patentService.batchClassify(file, summaryColumn, userId);

        if (result instanceof Map) {
            Map<String, Object> resultMap = (Map<String, Object>) result;
            Integer code = (Integer) resultMap.get("code");
            String message = (String) resultMap.get("message");
            Object data = resultMap.get("data");

            if (code != null && code == 200) {
                return ApiResponse.success(message, data);
            } else {
                return ApiResponse.error(code != null ? code : 500, message != null ? message : "批量分类失败");
            }
        }

        return ApiResponse.success("批量分类完成", result);
    }
}

