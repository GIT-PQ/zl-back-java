package com.pq.zlbackjava.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pq.zlbackjava.dto.ExcelPreviewResult;
import com.pq.zlbackjava.dto.PatentClassifyRequest;
import com.pq.zlbackjava.entity.ClassificationRecord;
import com.pq.zlbackjava.service.ClassificationRecordService;
import com.pq.zlbackjava.service.PatentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PatentServiceImpl implements PatentService {

    @Value("${python.service.url:http://localhost:5000}")
    private String pythonServiceUrl;

    private final RestTemplate restTemplate;
    private final ClassificationRecordService recordService;
    private final ObjectMapper objectMapper;

    @Autowired
    public PatentServiceImpl(ClassificationRecordService recordService, ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.recordService = recordService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Object classify(PatentClassifyRequest request, int userId) {
        try {
            String url = pythonServiceUrl + "/predict";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("summary", request.getSummary());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> result = response.getBody();

            // 分类成功后存库
            if (result != null) {
                Integer code = result.get("code") instanceof Number ? ((Number) result.get("code")).intValue() : null;
                if (code != null && code == 200) {
                    saveRecord(userId, request.getSummary(), result, "single", null);
                }
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "调用Python服务失败: " + e.getMessage());
            errorResponse.put("data", null);
            return errorResponse;
        }
    }

    @Override
    public ExcelPreviewResult parseExcel(MultipartFile file) {
        ExcelPreviewResult result = new ExcelPreviewResult();
        List<Map<String, Object>> allRows = new ArrayList<>();
        List<String> columns = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            // 使用EasyExcel读取，监听器模式
            EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
                private boolean headerParsed = false;

                @Override
                public void invokeHead(Map<Integer, String> headMap, AnalysisContext context) {
                    if (!headerParsed) {
                        // 获取列名（按顺序）
                        int maxIndex = headMap.keySet().stream().max(Integer::compareTo).orElse(0);
                        for (int i = 0; i <= maxIndex; i++) {
                            String colName = headMap.get(i);
                            columns.add(colName != null ? colName : "列" + (i + 1));
                        }
                        headerParsed = true;
                    }
                }

                @Override
                public void invoke(Map<Integer, String> data, AnalysisContext context) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 0; i < columns.size(); i++) {
                        String value = data.get(i);
                        row.put(columns.get(i), value != null ? value : "");
                    }
                    allRows.add(row);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                }
            }).sheet().doRead();

            result.setColumns(columns);
            result.setTotalRows(allRows.size());
            // 只返回前5行预览
            result.setPreviewRows(allRows.stream().limit(5).collect(Collectors.toList()));

        } catch (IOException e) {
            log.error("解析Excel文件失败", e);
            throw new RuntimeException("文件解析失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Object batchClassify(MultipartFile file, String summaryColumn, int userId) {
        List<Map<String, Object>> allRows = new ArrayList<>();
        List<String> columns = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
                private boolean headerParsed = false;

                @Override
                public void invokeHead(Map<Integer, String> headMap, AnalysisContext context) {
                    if (!headerParsed) {
                        int maxIndex = headMap.keySet().stream().max(Integer::compareTo).orElse(0);
                        for (int i = 0; i <= maxIndex; i++) {
                            String colName = headMap.get(i);
                            columns.add(colName != null ? colName : "列" + (i + 1));
                        }
                        headerParsed = true;
                    }
                }

                @Override
                public void invoke(Map<Integer, String> data, AnalysisContext context) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 0; i < columns.size(); i++) {
                        String value = data.get(i);
                        row.put(columns.get(i), value != null ? value : "");
                    }
                    allRows.add(row);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                }
            }).sheet().doRead();
        } catch (IOException e) {
            log.error("解析Excel文件失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "文件解析失败: " + e.getMessage());
            return errorResponse;
        }

        // 检查摘要列是否存在
        if (!columns.contains(summaryColumn)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", "文件中不存在列: " + summaryColumn);
            return errorResponse;
        }

        // 检查数量限制
        if (allRows.size() > 50) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", "单次最多处理50条，请拆分文件。当前: " + allRows.size() + "条");
            return errorResponse;
        }

        // 生成batch_id
        String batchId = UUID.randomUUID().toString();

        List<Map<String, Object>> results = new ArrayList<>();
        List<Map<String, Object>> failedList = new ArrayList<>();
        List<ClassificationRecord> successRecords = new ArrayList<>();

        for (Map<String, Object> row : allRows) {
            String summary = (String) row.get(summaryColumn);

            if (summary == null || summary.trim().isEmpty()) {
                Map<String, Object> failedItem = new HashMap<>();
                failedItem.put("summary", "");
                failedItem.put("error", "摘要内容为空");
                failedList.add(failedItem);
                continue;
            }

            try {
                String url = pythonServiceUrl + "/predict";
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("summary", summary);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
                Map<String, Object> result = response.getBody();

                if (result != null) {
                    Integer code = result.get("code") instanceof Number ? ((Number) result.get("code")).intValue() : null;
                    if (code != null && code == 200) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> data = (Map<String, Object>) result.get("data");
                        if (data != null) {
                            // 构建结果
                            Map<String, Object> successItem = new HashMap<>();
                            successItem.put("summary", summary);
                            successItem.put("predLabel", data.get("pred_label"));
                            successItem.put("predIndex", data.get("pred_index"));
                            successItem.put("predProbability", data.get("pred_probability"));
                            results.add(successItem);

                            // 构建记录
                            ClassificationRecord record = new ClassificationRecord();
                            record.setUserId(userId);
                            record.setSummary(summary);
                            record.setPredLabel((String) data.get("pred_label"));
                            record.setPredIndex(data.get("pred_index") instanceof Number ? ((Number) data.get("pred_index")).intValue() : 0);
                            record.setPredProbability(((Number) data.get("pred_probability")).doubleValue());
                            record.setSource("batch");
                            record.setBatchId(batchId);

                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> categories = (List<Map<String, Object>>) data.get("categories");
                            if (categories != null) {
                                record.setTopCategories(objectMapper.writeValueAsString(categories));
                            }
                            successRecords.add(record);
                        }
                    } else {
                        Map<String, Object> failedItem = new HashMap<>();
                        failedItem.put("summary", summary);
                        failedItem.put("error", "Python服务返回错误");
                        failedList.add(failedItem);
                    }
                }
            } catch (Exception e) {
                log.error("分类失败: {}", summary, e);
                Map<String, Object> failedItem = new HashMap<>();
                failedItem.put("summary", summary);
                failedItem.put("error", "Python服务调用失败: " + e.getMessage());
                failedList.add(failedItem);
            }
        }

        // 批量存库
        if (!successRecords.isEmpty()) {
            try {
                recordService.batchInsert(successRecords);
            } catch (Exception e) {
                log.error("批量存库失败", e);
            }
        }

        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "批量分类完成");

        Map<String, Object> data = new HashMap<>();
        data.put("batchId", batchId);
        data.put("total", allRows.size());
        data.put("success", results.size());
        data.put("failed", failedList.size());
        data.put("results", results);
        data.put("failedList", failedList);
        response.put("data", data);

        return response;
    }

    @SuppressWarnings("unchecked")
    private void saveRecord(int userId, String summary, Map<String, Object> result, String source, String batchId) {
        try {
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            if (data == null) {
                return;
            }

            ClassificationRecord record = new ClassificationRecord();
            record.setUserId(userId);
            record.setSummary(summary);
            record.setPredLabel((String) data.get("pred_label"));
            record.setPredIndex(data.get("pred_index") instanceof Number ? ((Number) data.get("pred_index")).intValue() : 0);
            record.setPredProbability(((Number) data.get("pred_probability")).doubleValue());
            record.setSource(source);
            record.setBatchId(batchId);

            List<Map<String, Object>> categories = (List<Map<String, Object>>) data.get("categories");
            if (categories != null) {
                record.setTopCategories(objectMapper.writeValueAsString(categories));
            }

            recordService.save(record);
        } catch (Exception e) {
            log.error("分类记录存库失败", e);
        }
    }
}