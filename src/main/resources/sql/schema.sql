-- ============================================
-- 医疗器械发明专利分类系统 数据库初始化脚本
-- 数据库: zl
-- ============================================

CREATE DATABASE IF NOT EXISTS zl DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE zl;

-- ============================================
-- 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS `user` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `question` VARCHAR(200) NOT NULL COMMENT '安全问题',
    `answer` VARCHAR(200) NOT NULL COMMENT '安全问题答案',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 分类记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `classification_record` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` INT(11) NOT NULL DEFAULT 0 COMMENT '用户ID，0表示匿名用户',
    `summary` TEXT NOT NULL COMMENT '输入的专利摘要文本',
    `pred_label` VARCHAR(100) NOT NULL COMMENT '预测类别名称',
    `pred_index` INT(11) NOT NULL COMMENT '预测类别索引(0-21)',
    `pred_probability` DOUBLE NOT NULL COMMENT '预测置信度(0.0-1.0)',
    `top_categories` JSON DEFAULT NULL COMMENT '全部22类概率分布(JSON数组，按概率降序)',
    `source` VARCHAR(20) NOT NULL DEFAULT 'single' COMMENT '来源: single-单条分类, batch-批量分类',
    `batch_id` VARCHAR(50) DEFAULT NULL COMMENT '批量分类批次ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分类时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_batch_id` (`batch_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类记录表';