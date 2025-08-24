# 1) Frontend Build
FROM node:20-alpine AS fe-builder
WORKDIR /frontend
COPY fileblock-frontend/package*.json ./
RUN npm ci
COPY fileblock-frontend/ ./
RUN npm run build  # 결과물: /frontend/dist

# 2) Backend Build (JDK 24)
FROM eclipse-temurin:24-jdk AS be-builder
WORKDIR /backend
COPY fileblock-backend/ ./
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 3) Runtime (JRE 24)
FROM eclipse-temurin:24-jre
WORKDIR /app
# 백엔드 JAR
COPY --from=be-builder /backend/build/libs/*.jar /app/app.jar
# 프론트 정적 파일
COPY --from=fe-builder /frontend/dist /app/static

# Spring이 로컬 디렉토리 정적 리소스를 서빙하도록
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
