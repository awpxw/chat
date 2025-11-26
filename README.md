# Chat（即时通讯）
> 真正零配置 · 一键启动 · 生产级发版

## 一句话本地启动（5 秒全套）

```bash
# 1. 启动全部中间件（MySQL、Redis、Nacos、MinIO、Kafka 等）
docker-compose up -d

# 2. 启动任意微服务（无需本地 application.yml）
mvn spring-boot:run
# 或
java -jar target/auth.jar
```

所有配置已集中到 Nacos + 环境变量，**克隆即跑！**

## 中间件总览（内网地址）

| 中间件       | 访问地址                                   | 默认账号 / 密码              | 宿主机端口      | 数据目录                     | 备注                   |
|--------------|--------------------------------------------|------------------------------|-----------------|------------------------------|------------------------|
| Nacos        | http://192.168.91.128:8848/nacos           | nacos / nacos                | 8848            | `/data/service/nacos`        | 配置中心 + 注册中心    |
| MySQL        | http://192.168.91.129:3306                        | root / 123456                | 3306            | `/data/service/mysql`        | 主数据库               |
| Redis        | http://192.168.91.129:6379                        | 123456                       | 6379            | `/data/service/redis`        | 缓存 + 分布式锁        |
| MinIO        | http://192.168.91.129:9000                 | minioadmin / minioadmin      | 9000            | `/data/service/minio`        | 对象存储               |
| Kafka        | http://192.168.91.128:9092                        | 无                           | 9092            | `/data/service/kafka`        | 消息队列               |
| Prometheus   | http://192.168.91.128:9090                 | 无                           | 9090            | `/data/service/prometheus`   | 监控采集               |
| Grafana      | http://192.168.91.128:3000                 | admin / admin123             | 3000            | `/data/service/grafana`      | 监控大盘（唯一 UI）    |
| ClickHouse   | http://192.168.91.128:8123                 | default / （空）             | 8123 / 9001     | `/data/service/clickhouse`   | 日志/实时分析          |

## Nacos 公共配置（common.yaml）终极版
已全面支持环境变量覆盖（本地默认值，生产只改变量）

> 位置：`dev` 命名空间 → `DataId: common.yaml`  
> 必须配合 `bootstrap.yml` 中的 `shared-configs` 加载

```yaml
spring:
  # （支持环境变量覆盖）
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:192.168.91.129}:${MYSQL_PORT:3306}/chat?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:123456}
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 300000
      connection-timeout: 30000
      validation-timeout: 5000
      max-lifetime: 1800000
      connection-test-query: SELECT 1
```

## 生产环境部署（只需改这几行）

```bash
export MYSQL_HOST=172.16.10.100
export MYSQL_PASSWORD=s3cUr3_MySql_2025
export REDIS_HOST=172.16.10.101
export REDIS_PASSWORD=R3d1s_Str0ng_2025
```
