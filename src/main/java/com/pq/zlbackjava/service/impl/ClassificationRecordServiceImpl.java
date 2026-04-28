package com.pq.zlbackjava.service.impl;

import com.pq.zlbackjava.entity.ClassificationRecord;
import com.pq.zlbackjava.mapper.ClassificationRecordMapper;
import com.pq.zlbackjava.service.ClassificationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassificationRecordServiceImpl implements ClassificationRecordService {

    @Autowired
    private ClassificationRecordMapper classificationRecordMapper;

    @Override
    public boolean save(ClassificationRecord record) {
        return classificationRecordMapper.insert(record) > 0;
    }

    @Override
    public boolean batchInsert(List<ClassificationRecord> records) {
        if (records == null || records.isEmpty()) {
            return false;
        }
        return classificationRecordMapper.batchInsert(records) > 0;
    }
}