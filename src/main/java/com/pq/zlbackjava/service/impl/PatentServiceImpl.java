package com.pq.zlbackjava.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PatentServiceImpl implements PatentService {

    @Value("${python.service.url:http://localhost:5000}")
    private String pythonServiceUrl;

    private final RestTemplate restTemplate;

    @Autowired
    private ClassificationRecordService recordService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PatentServiceImpl() {
        this.restTemplate = new RestTemplate();
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
                Integer code = (Integer) result.get("code");
                if (code != null && code == 200) {
                    saveRecord(userId, request.getSummary(), result);
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

    @SuppressWarnings("unchecked")
    private void saveRecord(int userId, String summary, Map<String, Object> result) {
        try {
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            if (data == null) {
                return;
            }

            ClassificationRecord record = new ClassificationRecord();
            record.setUserId(userId);
            record.setSummary(summary);
            record.setPredLabel((String) data.get("pred_label"));
            record.setPredIndex((Integer) data.get("pred_index"));
            record.setPredProbability(((Number) data.get("pred_probability")).doubleValue());
            record.setSource("single");

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