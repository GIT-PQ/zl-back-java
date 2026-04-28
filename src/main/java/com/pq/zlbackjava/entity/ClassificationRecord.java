package com.pq.zlbackjava.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ClassificationRecord {
    private Long id;
    private Integer userId;
    private String summary;
    private String predLabel;
    private Integer predIndex;
    private Double predProbability;
    private String topCategories;
    private String source;
    private String batchId;
    private Date createTime;
}