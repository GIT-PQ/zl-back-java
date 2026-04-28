package com.pq.zlbackjava.mapper;

import com.pq.zlbackjava.entity.ClassificationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassificationRecordMapper {
    int insert(ClassificationRecord record);

    int batchInsert(@Param("list") List<ClassificationRecord> records);

    List<ClassificationRecord> selectByCondition(@Param("userId") Integer userId,
                                                  @Param("predLabel") String predLabel,
                                                  @Param("startTime") String startTime,
                                                  @Param("endTime") String endTime,
                                                  @Param("source") String source,
                                                  @Param("summary") String summary,
                                                  @Param("batchId") String batchId);

    ClassificationRecord selectById(@Param("id") Long id);

    int countByCondition(@Param("userId") Integer userId,
                         @Param("predLabel") String predLabel,
                         @Param("startTime") String startTime,
                         @Param("endTime") String endTime,
                         @Param("source") String source,
                         @Param("summary") String summary,
                         @Param("batchId") String batchId);
}