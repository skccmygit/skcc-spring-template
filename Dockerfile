FROM eclipse-temurin:21-jdk-alpine AS builder

# 작업 디렉토리 생성
WORKDIR /app

# 인증서 복사
COPY docs/ssl/sk.crt /tmp/sk.crt

# JDK 인증서 저장소에 인증서 추가
RUN keytool -importcert -trustcacerts -noprompt \
    -keystore "$JAVA_HOME/lib/security/cacerts" \
    -storepass changeit \
    -alias sk-cert \
    -file /tmp/sk.crt

# Gradle Wrapper 및 소스 파일 복사
COPY gradlew settings.gradle build.gradle /app/
COPY gradle /app/gradle
COPY src /app/src

# Gradle Wrapper의 실행 권한 추가 및 애플리케이션 빌드 실행
RUN chmod +x ./gradlew
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jdk-alpine
# JAR 파일 복사
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
# Docker 컨테이너 실행 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

