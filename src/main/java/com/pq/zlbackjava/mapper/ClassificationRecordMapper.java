package com.pq.zlbackjava.mapper;

import com.pq.zlbackjava.entity.ClassificationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassificationRecordMapper {
    int insert(ClassificationRecord record);

    List<ClassificationRecord> selectByCondition(@Param("userId") Integer userId,
                                                  @Param("predLabel") String predLabel,
                                                  @Param("startTime") String startTime,
                                                  @Param("endTime") String endTime,
                                                  @Param("source") String source,
                                                  @Param("summary") String summary);

    ClassificationRecord selectById(@Param("id") Long id);
}