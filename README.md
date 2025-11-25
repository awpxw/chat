# Chat 项目全中间件资源清单

## 服务总览

| 服务名称       | 镜像版本                            | 宿主机端口          | 访问地址                              | 默认账号/密码                     | 数据持久化目录                  | 备注                              |
|----------------|-------------------------------------|---------------------|---------------------------------------|-----------------------------------|--------------------------|-----------------------------------|
| MySQL         | mysql:8.0                           | 3306                | localhost:3306                        | root / 123456                     | /data/service/mysql      | 主数据库                          |
| Redis         | redis:7-alpine                      | 6379                | localhost:6379                        | 密码：123456                      | /data/service/redis      | 缓存 + 分布式锁                   |
| MinIO         | minio/minio:latest                  | 9000                | http://localhost:9000                 | admin / admin123456               | /data/service/minio      | S3 兼容对象存储                   |
| Nacos         | nacos-registry.cn-hangzhou.cr.aliyuncs.com/nacos/nacos-server:v2.3.2 | 8848           | http://localhost:8848/nacos           | nacos / nacos                     | /data/service/nacos/logs | 注册中心 + 配置中心               |
| Zookeeper     | confluentinc/cp-zookeeper:7.6.0     | -（内部2181）       | -                                     | 无                                | 无需持久化                    | Kafka 依赖                        |
| Kafka         | confluentinc/cp-kafka:7.6.0         | 9092                | localhost:9092                        | 无                                | /data/service/kafka      | 消息队列                          |
| Prometheus    | prom/prometheus:latest              | 9090                | http://localhost:9090                 | 无                                | /data/service/prometheus | 指标采集                          |
| Grafana       | grafana/grafana:latest              | 3000                | http://localhost:3000                 | admin / admin123                  | /data/service/grafana    | 可视化大盘（唯一保留 UI）         |
| ClickHouse    | clickhouse/clickhouse-server:latest | 8123（HTTP）<br>9001（TCP） | http://localhost:8123<br>tcp://localhost:9001 | default / （空）                  | /data/service/clickhouse | 实时分析/日志存储                 |

## 常用连接字符串（直接复制到 application.yml）

```yaml
# MySQL
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/chat?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456

# Redis
  redis:
    host: localhost
    port: 6379
    password: 123456

# MinIO
minio:
  endpoint: http://localhost:9000
  access-key: admin
  secret-key: admin123456
  bucket: chat

# Nacos
spring:
  cloud:
    nacos:
      server-addr: localhost:8848
      username: nacos
      password: nacos

# Kafka
spring:
  kafka:
    bootstrap-servers: localhost:9092

# ClickHouse（示例）
clickhouse:
  address: localhost:8123
  username: default
  password: ''