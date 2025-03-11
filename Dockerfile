FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

RUN apk add --no-cache wget unzip

RUN wget https://services.gradle.org/distributions/gradle-8.2.1-bin.zip && \
    unzip gradle-8.2.1-bin.zip -d /opt/gradle && \
    rm gradle-8.2.1-bin.zip

ENV GRADLE_HOME=/opt/gradle/gradle-8.2.1
ENV PATH="/opt/gradle/gradle-8.2.1/bin:${PATH}"

COPY . .
RUN gradle clean build --no-daemon

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/chatapp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dserver.port=$PORT", "-jar", "/app/app.jar"]
