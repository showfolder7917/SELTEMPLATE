# 用户 API 说明

## 模块定位

- 根包固定为 `com.sp.selfsp`
- 用户模块目录固定为 `com.sp.selfsp.user`
- 输入对象放在 `domain.in`
- 输出对象放在 `domain.out`
- 持久化对象放在 `domain`

## 已创建接口

- `GET /api/users` 查询用户列表
- `GET /api/users/{id}` 查询单个用户
- `POST /api/users` 新增用户
- `PUT /api/users/{id}` 更新用户
- `DELETE /api/users/{id}` 删除用户

## 数据模型

- 表名：`sys_user`
- 唯一键：`email`
- 主键：`id`，由 H2 数据库自增生成

## 配置文件

- Spring Boot 主配置文件：`src/main/resources/application.properties`
- 当前数据源：H2 内存库 `jdbc:h2:mem:selsp`

## 数据库设计文档

- 正式数据库详细设计文档：`doc/数据库设计文档/数据库详细设计.md`
- 正式数据库设计工作簿：`doc/aip接口文档/用户api/SELSP_数据库设计文档.xlsx`
- 数据修正或新增数据时，必须同步修正文档：`doc/rule/数据变更同步规则.md`

## 详细设计文档

- 正式用户详细设计工作簿：`doc/aip接口文档/用户api/SELSP_用户详细设计文档.xlsx`
