package com.pq.zlbackjava.service;

import com.pq.zlbackjava.dto.ExcelPreviewResult;
import com.pq.zlbackjava.dto.PatentClassifyRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 专利服务接口
 */
public interface PatentService {
    /**
     * 专利分类预测
     * @param request 预测请求
     * @return 预测结果
     */
    Object classify(PatentClassifyRequest request, int userId);

    /**
     * 解析Excel文件，返回列名和预览数据
     * @param file 上传的Excel文件
     * @return 预览结果
     */
    ExcelPreviewResult parseExcel(MultipartFile file);

    /**
     * 批量分类
     * @param file 上传的Excel文件
     * @param summaryColumn 摘要列名
     * @param userId 用户ID
     * @return 批量分类结果
     */
    Object batchClassify(MultipartFile file, String summaryColumn, int userId);
}

