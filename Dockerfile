FROM maven:3.9.1-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .

ARG MODULE
# 终极无敌编译命令（永别依赖找不到）
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -U -pl $MODULE -amd clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ARG MODULE
COPY --from=builder /app/${MODULE}/target/*-SNAPSHOT.jar /app/chat-${MODULE}.jar

# 自动提取端口
RUN PORT=$(grep -h "server.port" ${MODULE}/src/main/resources/application*.yml \
  ${MODULE}/src/main/resources/application*.properties 2>/dev/null \
  | head -1 | grep -oE '[0-9]+' || echo 8080) && \
  echo "服务 $MODULE 启动端口: $PORT"

EXPOSE ${PORT:-8080}
ENTRYPOINT ["java", "-jar", "/app/chat-${MODULE}.jar"]