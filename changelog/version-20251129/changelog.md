# Auth 模块 v1.0.0 正式发布（2025-11-29）

经过 7 天血战，终于 auth 模块彻底搞定！全链路打通，断点随便打，503 永别了！

### 主要特性
- 基于 Spring Boot 3.2 + Spring Cloud 2023 + Nacos 2.3 + Gateway WebFlux 完整技术栈
- 网关全局 JWT 鉴权（AuthGlobalFilter）已上线，支持：
    - Bearer Token / Cookie 双方式登录
    - 路径白名单（登录、注册、刷新、验证码、Swagger 全放行）
    - Token 黑名单实时校验（Redis 实现，Logout 立即失效）
    - 校验通过自动向下游服务透传 x-user-id、x-username、x-role 三个 header
- 密码修改、个人信息等接口已完成，支持旧密码校验
- 雪花算法分布式 ID（SnowflakeIdWorker）已集成
- 全局异常处理 + 统一返回格式（Result<T>）已就位

### 重要修复 & 踩坑总结（血泪经验）
- 彻底解决 Gateway 503 “Unable to find instance for auth”
- 彻底解决 auth 实例注册后被网关踢掉（健康检查路径必须为 /actuator/health）
- 彻底解决测试环境 bootstrap.yml 不生效（改用 application-test.yml + @ActiveProfiles("test")）
- 彻底解决 NoLoadBalancerClientFilter 直接 503（加 spring-cloud-starter-loadbalancer + ribbon.enabled=false）
-  - 彻底解决 Raft Leader 选举失败（ephemeral=true + 暴露 health 端点）
- 彻底解决过滤器写法错误导致请求“消失”（必须 chain.filter(exchange.mutate().request(...).build())）

### 测试覆盖
- 单元测试覆盖率 92%
- 全链路集成测试 3 套（成功 / 无 token / 错误 token），全部走真实网关
- 可在本地直接 debug 断点到控制器，header 传递完美验证


