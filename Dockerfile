FROM eclipse-temurin:17-jre-alpine AS runtime

# 接收构建参数（模块名）
ARG MODULE
WORKDIR /app

# 复制对应模块的可执行 jar（名字带模块名，启动日志清晰）
COPY ${MODULE}/target/*-SNAPSHOT.jar /app/chat-${MODULE}.jar

# 自动提取 server.port（支持 application.yml 和 application.properties）
RUN PORT=$( \
    grep -h "server.port" ${MODULE}/src/main/resources/application*.yml \
    ${MODULE}/src/main/resources/application*.properties 2>/dev/null \
    | head -1 | grep -oE '[0-9]+' || echo 8080 \
  ) && \
  echo "服务 $MODULE 启动端口: $PORT" && \
  echo "PORT=$PORT" > /app/.env

EXPOSE ${PORT:-8080}

ENTRYPOINT ["java", "-jar", "/app/chat-${MODULE}.jar"]