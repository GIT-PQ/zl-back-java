package com.pq.zlbackjava.service;

import com.pq.zlbackjava.dto.PatentClassifyRequest;

/**
 * 专利服务接口
 */
public interface PatentService {
    /**
     * 专利分类预测
     * @param request 预测请求
     * @return 预测结果
     */
    Object classify(PatentClassifyRequest request);
}

