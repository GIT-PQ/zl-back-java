package com.pq.zlbackjava.service.impl;

import com.pq.zlbackjava.entity.ClassificationRecord;
import com.pq.zlbackjava.mapper.ClassificationRecordMapper;
import com.pq.zlbackjava.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private ClassificationRecordMapper classificationRecordMapper;

    @Override
    public List<ClassificationRecord> listByCondition(Integer userId, String predLabel, String startTime, String endTime, String source, String summary, String batchId) {
        // 在 Service 层转义 LIKE 通配符
        String escapedSummary = null;
        if (summary != null && !summary.isEmpty()) {
            escapedSummary = summary.replace("%", "\\%").replace("_", "\\_");
        }
        return classificationRecordMapper.selectByCondition(userId, predLabel, startTime, endTime, source, escapedSummary, batchId);
    }

    @Override
    public ClassificationRecord getById(Long id, Integer userId) {
        ClassificationRecord record = classificationRecordMapper.selectById(id);
        if (record != null && !record.getUserId().equals(userId)) {
            return null;
        }
        return record;
    }

    @Override
    public int countByCondition(Integer userId, String predLabel, String startTime, String endTime, String source, String summary, String batchId) {
        String escapedSummary = null;
        if (summary != null && !summary.isEmpty()) {
            escapedSummary = summary.replace("%", "\\%").replace("_", "\\_");
        }
        return classificationRecordMapper.countByCondition(userId, predLabel, startTime, endTime, source, escapedSummary, batchId);
    }

    @Override
    public List<ClassificationRecord> queryAll(Integer userId, String predLabel, String startTime, String endTime, String source, String summary, String batchId) {
        String escapedSummary = null;
        if (summary != null && !summary.isEmpty()) {
            escapedSummary = summary.replace("%", "\\%").replace("_", "\\_");
        }
        return classificationRecordMapper.selectByCondition(userId, predLabel, startTime, endTime, source, escapedSummary, batchId);
    }
}
