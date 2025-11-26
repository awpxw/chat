# Chat 微服务项目（Spring Cloud Alibaba）一键启动版

## 一句话启动（本地开发）

```bash
# 1. 启动所有中间件（含 MySQL、Redis、Nacos、MinIO 等）
docker-compose up -d

# 2. 启动任意微服务（零本地配置）
mvn spring-boot:run
# 或
java -jar auth.jar



中间件,访问地址,默认账号 / 密码,宿主机端口,数据目录,备注
Nacos,http://192.168.91.128:8848/nacos,nacos / nacos,8848,/data/service/nacos,配置中心 + 注册中心
MySQL,192.168.91.129:3306,root / 123456,3306,/data/service/mysql,主数据库
Redis,192.168.91.129:6379,123456,6379,/data/service/redis,缓存 + 分布式锁
MinIO,http://192.168.91.129:9000,minioadmin / minioadmin,9000,/data/service/minio,对象存储
Kafka,192.168.91.128:9092,无,9092,/data/service/kafka,消息队列
Prometheus,http://192.168.91.128:9090,无,9090,/data/service/prometheus,监控采集
Grafana,http://192.168.91.128:3000,admin / admin123,3000,/data/service/grafana,监控大盘（唯一保留 UI）
ClickHouse,http://192.168.91.128:8123,default / （空）,8123 / 9001,/data/service/clickhouse,日志分析


Nacos 公共配置（common.yaml）终极版（已支持环境变量覆盖）
> 位置：dev 命名空间 → common.yaml（必须配合 bootstrap.yml 中的 shared-configs 加载）

spring:
  # ==================== MySQL（支持环境变量覆盖）===================
  datasource:
    url: jdbc:mysql://$$ {MYSQL_HOST:192.168.91.129}: $${MYSQL_PORT:3306}/chat?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true
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

  # ==================== Redis（Lettuce + Redisson 共用）===================
  redis:
    host: ${REDIS_HOST:192.168.91.129}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:123456}
    database: ${REDIS_DATABASE:0}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5

  # Redisson（自动读取 spring.redis，可省略）
  redisson:
    address: redis://$$ {REDIS_HOST:192.168.91.129}: $${REDIS_PORT:6379}
    database: ${REDIS_DATABASE:0}
    password: ${REDIS_PASSWORD:123456}

  # ==================== MinIO ====================
  minio:
    endpoint: http://${MINIO_HOST:192.168.91.129}:9000
    access-key: ${MINIO_ACCESS_KEY:minioadmin}
    secret-key: ${MINIO_SECRET_KEY:minioadmin}
    bucket-name: ${MINIO_BUCKET:mall}

  # ==================== MyBatis-Plus ====================
  mybatis-plus:
    global-config:
      db-config:
        id-type: assign_id
        logic-delete-field: deleted
        logic-delete-value: 1
        logic-not-delete-value: 0
    configuration:
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl   # 开发环境打印 SQL
      mapper-locations: classpath*:mapper/**/*Mapper.xml
      type-aliases-package: com.aw.**.domain