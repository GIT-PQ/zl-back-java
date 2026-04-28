package com.pq.zlbackjava.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Excel预览结果DTO
 */
@Data
public class ExcelPreviewResult {
    private List<String> columns;
    private List<Map<String, Object>> previewRows;
    private Integer totalRows;
}