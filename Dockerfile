# 1단계: Build Stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Gradle 캐시 활용을 위해 gradlew와 설정 파일 먼저 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies || true

# 소스 코드 복사 및 빌드
COPY . .
RUN ./gradlew --no-daemon clean build -x test



# 2단계: Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache wget ca-certificates

# 빌드 산출물만 복사
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

# OpenTelemetry Java Agent 다운로드
RUN wget -O opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar

# 로그/포트 준비
RUN mkdir -p /opt/app/logs
VOLUME ["/opt/app/logs"]
EXPOSE 8080


# 환경 변수 설정 (나중에 docker run 시에 덮어쓸 수도 있음)
ENV OTEL_SERVICE_NAME=point-live-young
ENV OTEL_EXPORTER_OTLP_ENDPOINT=
ENV SPRING_PROFILES_ACTIVE=prod

# 애플리케이션 실행
ENTRYPOINT ["java", \
  "-javaagent:/app/opentelemetry-javaagent.jar", \
  "-Dotel.service.name=${OTEL_SERVICE_NAME}", \
  "-Dotel.instrumentation.logback-mdc.enabled=true", \
  "-Dotel.exporter.otlp.endpoint=${OTEL_EXPORTER_OTLP_ENDPOINT}", \
  "-Dotel.exporter.otlp.protocol=grpc", \
  "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
  "-jar", "app.jar"]