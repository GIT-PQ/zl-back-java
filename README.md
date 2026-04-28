# zl-back-java - 专利分类系统 Java 后端

基于 Spring Boot 的专利分类系统后端服务，提供用户认证、专利分类接口代理、分类记录管理等功能。

## 技术栈

- Spring Boot 2.6.13
- MyBatis 2.2.2
- MySQL Connector
- Lombok
- EasyExcel 3.1.1（Excel 导出）

## 项目结构

```
src/main/java/com/pq/zlbackjava/
├── controller/           # 控制器
│   ├── UserController.java
│   ├── PatentController.java
│   └── RecordController.java
├── service/              # 服务层
├── mapper/               # MyBatis Mapper
├── entity/               # 实体类
├── dto/                  # 数据传输对象
├── vo/                   # 视图对象
└── ZlBackJavaApplication.java
```

## 快速开始

### 环境要求

- Java 8+
- Maven 3.6+
- MySQL 5.7+

### 配置数据库

1. 创建数据库 `zl`
2. 执行 `mysql/user.sql` 或 `src/main/resources/sql/schema.sql`

### 启动服务

```bash
mvn spring-boot:run
```

服务启动于 http://localhost:8081

### 打包部署

```bash
mvn clean package
```

生成 jar 文件于 `target/zl-back-java-0.0.1-SNAPSHOT.jar`

## API 接口

### 用户认证 (/api/auth)

| 接口 | 方法 | 说明 | 参数 |
|------|------|------|------|
| /login | POST | 登录 | username, password |
| /register | POST | 注册 | username, password, question, answer |
| /security-question | GET | 获取安全问题 | username |
| /verify-security-question | POST | 验证安全问题 | username, answer |
| /reset-password | POST | 重置密码 | username, newPassword |
| /user | GET | 获取用户信息 | - |
| /logout | POST | 退出登录 | - |

### 专利分类 (/api/patent)

| 接口 | 方法 | 说明 | 参数 |
|------|------|------|------|
| /classify | POST | 单条分类 | summary |
| /upload-preview | POST | Excel预览 | file |
| /batch-classify | POST | 批量分类 | file, summaryColumn |

### 分类记录 (/api/record)

| 接口 | 方法 | 说明 | 参数 |
|------|------|------|------|
| /list | GET | 获取列表 | userId, page, pageSize |
| /export | GET | 导出Excel | userId |

## 配置说明

`application.yaml` 关键配置：

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/zl
    username: root
    password: your_password

python:
  service:
    url: http://localhost:5000
```

## 数据库表

### user 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| username | VARCHAR | 用户名 |
| password | VARCHAR | 密码 |
| question | VARCHAR | 安全问题 |
| answer | VARCHAR | 安全答案 |

### classification_record 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| user_id | INT | 用户ID |
| summary | TEXT | 专利摘要 |
| pred_label | VARCHAR | 预测类别 |
| pred_probability | DOUBLE | 预测概率 |
| create_time | DATETIME | 创建时间 |

## 相关项目

- [zl-front](../zl-front) - Vue 前端
- [zl-back-py](../zl-back-py) - Python 模型服务