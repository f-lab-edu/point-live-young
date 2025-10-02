# 1단계: Build Stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Gradle 캐시 활용을 위해 gradlew와 설정 파일 먼저 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# 의존성 캐시를 위해 의존성만 미리 다운로드
RUN ./gradlew dependencies || return 0

# 소스 코드 복사 및 빌드
COPY . .
RUN ./gradlew clean build -x test

# 2단계: Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 빌드 산출물만 복사
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]