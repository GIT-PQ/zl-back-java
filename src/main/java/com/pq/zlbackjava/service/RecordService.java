package com.pq.zlbackjava.service;

import com.pq.zlbackjava.entity.ClassificationRecord;

import java.util.List;

public interface RecordService {
    List<ClassificationRecord> listByCondition(Integer userId, String predLabel, String startTime, String endTime, String source, String summary, String batchId);

    ClassificationRecord getById(Long id, Integer userId);

    int countByCondition(Integer userId, String predLabel, String startTime, String endTime, String source, String summary, String batchId);

    List<ClassificationRecord> queryAll(Integer userId, String predLabel, String startTime, String endTime, String source, String summary, String batchId);
}
