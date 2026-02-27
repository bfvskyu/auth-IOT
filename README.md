# 总结
## 一、 登录与身份认证逻辑总结 (Identity & Auth)
这部分的核心是解决：**“你是谁？”** 以及 **“我凭什么相信你？”**。

### 1. 图形验证码 (防止暴力破解)
* **后端流程：** 利用 Hutool 生成 `LineCaptcha`。

  * **存储：** 将验证码答案存入 Redis，并绑定一个唯一的 `uuid`。这里设置了有效期（如 60 分钟），到期自动销毁。

  * **传递：** 后端只返回图片 Base64 和 `uuid`。

* **前端流程：**

  * 页面加载时调用接口，展示图片并将 `uuid` 存入登录表单。

  * 点击图片触发刷新，重新获取。

### 2. 安全登录与 Token 签发 (Sa-Token + Redis)
* **双重校验：** 后端先从 Redis 取出 `uuid` 对应的答案比对验证码。比对成功后立即删除该验证码，防止重用。

* **身份核验：** 使用 `LambdaQueryWrapper` 查询数据库中的账号密码。

* **签发通行证：** 调用 `StpUtil.login(id)`。Sa-Token 会自动生成一串 `Token` 并存入 Redis 管理。

* **前端持久化：** 前端拿到 Token 后，存入 **Pinia** 状态库和 **localStorage** 中，确保刷新页面不会掉线。

## 二、 设备 CRUD 与分页查询总结 (Data Management)
这部分的核心是：**“高效、安全地处理业务数据”**。

### 1. 后端：MyBatis-Plus 的高效开发
* **实体映射：** `IotDevice` 实体类使用了 `@TableName` 和 `@TableId` 注解，建立了 Java 对象与数据库表的映射关系。

* **自动填充 (Magic)：** 通过 `MyMetaObjectHandler` 拦截器，实现了在执行 `INSERT` 时自动为 `createTime` 赋值。这样你的业务代码中不需要手动 `setCreateTime`。

* **物理分页：** 在 `MybatisPlusConfig` 中配置了 `PaginationInnerInterceptor`。当你调用 `page()` 方法时，拦截器会自动在 SQL 后面拼接 `LIMIT` 分页语句。

* **动态 SQL (LambdaQuery)：** 使用 `LambdaQueryWrapper` 避免了手写 SQL 字符串带来的错写风险。利用 `like` 等方法的第一个布尔参数，实现了“如果前端传了参数才进行搜索”的动态效果。

### 2. 前端：Element Plus 的交互设计
* **请求拦截器：** 在 Axios 封装中，利用拦截器自动从 Pinia 中提取 Token 并塞进请求头的 `Authorization` 字段中。

* **数据渲染：** 使用 `<el-table>` 绑定数据列表，配合 `<template #default="scope">` 实现了状态标签的彩色显示（如在线/离线）。

* **双向同步：** 分页器的 `pageNum` 和 `pageSize` 与查询参数双向绑定，每次点击页码都会重新触发 `fetchData()` 获取新数据。

## 三、 避坑与实战经验复盘
* **Redis 是“地基”：** 如果 Redis 没开，验证码接口和 Sa-Token 鉴权都会崩溃报 500 错误。

* **导包陷阱：** 在使用分页时，一定要分清 `com.baomidou.mybatisplus` 的 `Page` 和 `org.springframework.data` 的 `Page`。后者是接口，无法直接 `new`。

* **唯一约束：** 数据库中的 `UNIQUE KEY`（如设备 SN 码）会强制要求数据不重复，插入重复数据会报 `Duplicate entry` 错误。
