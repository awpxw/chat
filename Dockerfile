# 终极无敌版 Dockerfile（已修复镜像名）
FROM maven:3.9.1-eclipse-temurin-17 AS builder
WORKDIR /app

# 复制整个项目
COPY . .

# 接收模块名，编译并打包
ARG MODULE
RUN mvn -B -pl $MODULE -am clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ARG MODULE
COPY --from=builder /app/${MODULE}/target/*-SNAPSHOT.jar /app/chat-${MODULE}.jar

# 自动提取端口
RUN PORT=$(grep -h "server.port" ${MODULE}/src/main/resources/application*.yml \
  ${MODULE}/src/main/resources/application*.properties 2>/dev/null \
  | head -1 | grep -oE '[0-9]+' || echo 8080) && \
  echo "服务 $MODULE 启动端口: $PORT" && \
  echo "PORT=$PORT" > /app/.env

EXPOSE ${PORT:-8080}

ENTRYPOINT ["java", "-jar", "/app/chat-${MODULE}.jar"]