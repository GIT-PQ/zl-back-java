package com.pq.zlbackjava.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.pq.zlbackjava.entity.ClassificationRecord;
import lombok.Data;

import java.util.Date;

@Data
public class ClassificationRecordExcelVO {

    @ExcelProperty("序号")
    @ColumnWidth(8)
    private Integer rowNum;

    @ExcelProperty("专利摘要")
    @ColumnWidth(50)
    private String summary;

    @ExcelProperty("预测类别")
    @ColumnWidth(15)
    private String predLabel;

    @ExcelProperty("置信度")
    @ColumnWidth(10)
    private String predProbability;

    @ExcelProperty("来源")
    @ColumnWidth(12)
    private String sourceDisplay;

    @ExcelProperty("批次ID")
    @ColumnWidth(30)
    private String batchIdDisplay;

    @ExcelProperty("分类时间")
    @ColumnWidth(20)
    private String createTimeDisplay;

    public static ClassificationRecordExcelVO fromEntity(ClassificationRecord entity, int rowNum) {
        ClassificationRecordExcelVO vo = new ClassificationRecordExcelVO();
        vo.setRowNum(rowNum);
        vo.setSummary(entity.getSummary());
        vo.setPredLabel(entity.getPredLabel());
        vo.setPredProbability(formatProbability(entity.getPredProbability()));
        vo.setSourceDisplay(entity.getSource() == null ? "-" :
            (entity.getSource().equals("single") ? "单条输入" : "批量导入"));
        vo.setBatchIdDisplay(entity.getBatchId() == null ? "-" : entity.getBatchId());
        vo.setCreateTimeDisplay(formatDate(entity.getCreateTime()));
        return vo;
    }

    private static String formatProbability(Double probability) {
        if (probability == null) {
            return "-";
        }
        return String.format("%.2f%%", probability * 100);
    }

    private static String formatDate(Date date) {
        if (date == null) {
            return "-";
        }
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}