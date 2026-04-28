package com.pq.zlbackjava.controller;

import com.pq.zlbackjava.dto.ApiResponse;
import com.pq.zlbackjava.entity.ClassificationRecord;
import com.pq.zlbackjava.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/record")
@CrossOrigin(origins = "*")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @GetMapping("/list")
    public ApiResponse<List<ClassificationRecord>> list(
            @RequestParam(required = false) String predLabel,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String summary,
            @RequestHeader("Authorization") String authorization) {
        Integer userId = extractUserId(authorization);
        if (userId == null) {
            return ApiResponse.error(401, "未授权");
        }
        List<ClassificationRecord> records = recordService.listByCondition(userId, predLabel, startTime, endTime, source, summary);
        return ApiResponse.success("查询成功", records);
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassificationRecord> getDetail(@PathVariable Long id,
                                                       @RequestHeader("Authorization") String authorization) {
        Integer userId = extractUserId(authorization);
        if (userId == null) {
            return ApiResponse.error(401, "未授权");
        }
        ClassificationRecord record = recordService.getById(id, userId);
        if (record == null) {
            return ApiResponse.error(404, "记录不存在");
        }
        return ApiResponse.success("查询成功", record);
    }

    private Integer extractUserId(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try {
                String[] parts = token.split("_");
                if (parts.length >= 2) {
                    return Integer.parseInt(parts[1]);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
