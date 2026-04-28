package com.pq.zlbackjava.controller;

import com.alibaba.excel.EasyExcel;
import com.pq.zlbackjava.dto.ApiResponse;
import com.pq.zlbackjava.entity.ClassificationRecord;
import com.pq.zlbackjava.service.RecordService;
import com.pq.zlbackjava.vo.ClassificationRecordExcelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
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
            @RequestParam(required = false) String batchId,
            @RequestHeader("Authorization") String authorization) {
        Integer userId = extractUserId(authorization);
        if (userId == null) {
            return ApiResponse.error(401, "未授权");
        }
        List<ClassificationRecord> records = recordService.listByCondition(userId, predLabel, startTime, endTime, source, summary, batchId);
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

    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) String predLabel,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String summary,
            @RequestParam(required = false) String batchId,
            @RequestHeader("Authorization") String authorization,
            HttpServletResponse response) throws IOException {
        Integer userId = extractUserId(authorization);
        if (userId == null) {
            writeJsonError(response, 401, "未授权");
            return;
        }

        int count = recordService.countByCondition(userId, predLabel, startTime, endTime, source, summary, batchId);
        if (count == 0) {
            writeJsonError(response, 400, "暂无数据，请调整筛选条件");
            return;
        }
        if (count > 200) {
            writeJsonError(response, 400, "超过200条，请缩小筛选范围");
            return;
        }

        List<ClassificationRecord> records = recordService.queryAll(userId, predLabel, startTime, endTime, source, summary, batchId);
        List<ClassificationRecordExcelVO> excelData = new java.util.ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            excelData.add(ClassificationRecordExcelVO.fromEntity(records.get(i), i + 1));
        }

        String fileName = URLEncoder.encode("分类记录_" + new java.text.SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new java.util.Date()) + ".xlsx", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        EasyExcel.write(response.getOutputStream(), ClassificationRecordExcelVO.class)
                .sheet("分类记录")
                .doWrite(excelData);
    }

    private void writeJsonError(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + message + "\",\"data\":null}");
    }
}
