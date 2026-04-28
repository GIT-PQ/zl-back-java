package com.pq.zlbackjava.service;

import com.pq.zlbackjava.entity.ClassificationRecord;

import java.util.List;

public interface ClassificationRecordService {
    boolean save(ClassificationRecord record);

    boolean batchInsert(List<ClassificationRecord> records);
}