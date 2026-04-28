package com.pq.zlbackjava.mapper;

import com.pq.zlbackjava.entity.ClassificationRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClassificationRecordMapper {
    int insert(ClassificationRecord record);
}