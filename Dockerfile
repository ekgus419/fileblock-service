# 1단계: 프론트 빌드
FROM node:20 AS frontend-builder
WORKDIR /frontend
COPY fileblock-frontend/package*.json ./
RUN npm ci || npm i
COPY fileblock-frontend/ .
RUN npm run build

# 2단계: 백엔드 빌드 (Gradle)
FROM gradle:8-jdk17 AS backend-builder
WORKDIR /backend
COPY fileblock-backend/ .
RUN ./gradlew clean build -x test

# 3단계: 런타임
FROM eclipse-temurin:17-jre
WORKDIR /app

# JAR 복사 (build/libs 아래 생성된 *.jar 경로 맞춰야 함)
COPY --from=backend-builder /backend/build/libs/*.jar /app/app.jar

# 프론트 빌드 결과물을 /app/static 에 두고 Spring이 읽게 함
COPY --from=frontend-builder /frontend/dist /app/static

# Spring Boot 정적리소스 경로 지정
ENV SPRING_WEB_RESOURCES_STATIC_LOCATIONS=file:/app/static/
ENV SERVER_PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
