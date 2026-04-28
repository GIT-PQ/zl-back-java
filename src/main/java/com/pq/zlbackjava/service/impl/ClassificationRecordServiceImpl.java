package com.pq.zlbackjava.service.impl;

import com.pq.zlbackjava.entity.ClassificationRecord;
import com.pq.zlbackjava.mapper.ClassificationRecordMapper;
import com.pq.zlbackjava.service.ClassificationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassificationRecordServiceImpl implements ClassificationRecordService {

    @Autowired
    private ClassificationRecordMapper classificationRecordMapper;

    @Override
    public boolean save(ClassificationRecord record) {
        return classificationRecordMapper.insert(record) > 0;
    }
}