# 老人能力评估记录 Spring Boot 后台

评估表单实现，后端使用：

- Java 17
- Spring Boot 3.5.10
- Spring Security 6
- JWT 登录认证
- MyBatis-Plus
- PostgreSQL
- Maven

## 1. 创建数据库

```sql
CREATE DATABASE elderly_assessment;
```

然后执行：

```bash
psql -U postgres -d elderly_assessment -f src/main/resources/schema.sql
```

修改 `src/main/resources/application.yml` 中的数据库用户名和密码。

## 2. 启动

```bash
mvn clean spring-boot:run
```

或者：

```bash
mvn clean package
java -jar target/elderly-assessment-api-1.0.0.jar
```

默认端口：

```text
http://localhost:8080
```

## 3. 用户接口

### 注册

POST `/api/auth/register`

```json
{
  "username": "admin",
  "password": "123456",
  "realName": "管理员",
  "phone": "13800000000"
}
```

### 登录

POST `/api/auth/login`

```json
{
  "username": "admin",
  "password": "123456"
}
```

返回：

```json
{
  "token": "eyJ...",
  "user": {
    "id": 1,
    "username": "admin",
    "realName": "管理员"
  }
}
```

后续请求：

```http
Authorization: Bearer eyJ...
```

### 修改密码

PUT `/api/auth/password`

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

### 修改资料

PUT `/api/auth/profile?realName=张三&phone=13900000000`

## 4. 评估记录 CRUD

### 新增

POST `/api/assessments`

请求体与前端表单对应：

```json
{
  "no": "AS202609260001",
  "assessmentDate": "2026-09-26",
  "reason": "首次评估",
  "elderlyName": "张三",
  "gender": "男",
  "birthDate": "1940-01-01",
  "height": 170,
  "weight": 65,
  "ethnicity": "汉族",
  "religion": "无",
  "idNo": "xxxxxxxxxxxxxxxxxx",
  "education": "不详",
  "living": "[\"独居\"]",
  "marriage": "已婚",
  "risks": "{\"跌倒\":0,\"走失\":0,\"噎食\":0,\"自杀、自伤\":0,\"其他\":0}",
  "provider": "{\"name\":\"张三\",\"relation\":\"本人\",\"contact\":\"李四\",\"phone\":\"13800000000\"}",
  "disease": "[\"Hypertension I10-I15\"]",
  "diseaseOther": "",
  "medications": "[{\"name\":\"药物A\",\"method\":\"口服\",\"dose\":\"1片\",\"frequency\":\"每日1次\"}]",
  "health": "{\"pressure\":\"无\",\"joint\":\"没有影响日常生活功能\",\"pain\":\"无疼痛\",\"malnutrition\":\"无\",\"airway\":\"无\",\"coma\":\"无\"}",
  "answers": "{\"B1.1\":1,\"B1.2\":2}",
  "totalScore": 3,
  "initialLevel": 1,
  "finalLevel": 1
}
```

### 分页查询

GET `/api/assessments?current=1&size=10&keyword=张三`

支持按：

- 老人姓名
- 评估编号
- 公民身份号码

查询。

### 详情

GET `/api/assessments/{id}`

### 修改

PUT `/api/assessments/{id}`

请求体同新增。

### 删除

DELETE `/api/assessments/{id}`

## 5. 表单字段映射

后端已经覆盖你表单中的：

A.1 评估信息：
- 评估编号
- 评估基准日期
- 评估原因

A.2 老人基本信息：
- 姓名
- 性别
- 出生日期
- 身高
- 体重
- 民族
- 宗教信仰
- 公民身份号码
- 文化程度
- 居住情况
- 婚姻状况

A.2.14：
- 跌倒
- 走失
- 噎食
- 自杀、自伤
- 其他风险

A.3：
- 信息提供者
- 与老人关系
- 联系人
- 联系电话

A.4：
- 疾病
- 其他疾病
- 药物名称
- 服药方法
- 用药剂量
- 用药频率

A.5：
- 压力性损伤
- 关节/功能问题
- 疼痛
- 营养
- 气道
- 昏迷

B：
- 能力评估答案 answers
- 总分
- 初步等级
- 最终等级

这些复杂的多选、风险、疾病、药物、健康情况、评分答案先以 PostgreSQL `JSONB` 保存，便于与你当前 Vue 表单直接对接。

## 6. 关于评分规则

你上传的页面已经包含初步等级、昏迷直接完全丧失、风险事件等最终等级调整逻辑；本后端保存前端计算后的 `totalScore / initialLevel / finalLevel`。

如果需要把 GB/T 42195-2022 的评分规则完全迁移到后端，建议下一版增加：
- assessment_version
- assessment_category
- assessment_item
- assessment_option
- assessment_level_rule
- assessment_change_rule

由后端计算最终等级，避免前端篡改评分结果。

## 7. 安全说明

JWT 密钥仅用于开发示例，生产环境必须修改：

```yaml
app:
  jwt:
    secret: "change-this-secret-key-to-at-least-32-characters-long"
```

生产环境还建议增加：
- Redis Token 黑名单/注销
- 登录失败次数限制
- 验证码
- RBAC 用户/角色/权限
- 操作日志
- 数据权限
- HTTPS
