FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache wget unzip

RUN wget https://services.gradle.org/distributions/gradle-8.2.1-bin.zip && \
    unzip gradle-8.2.1-bin.zip -d /opt/gradle && \
    rm gradle-8.2.1-bin.zip

ENV GRADLE_HOME=/opt/gradle/gradle-8.2.1
ENV PATH="/opt/gradle/gradle-8.2.1/bin:${PATH}"

RUN mkdir -p /root/.gradle && chown -R root:root /root/.gradle

COPY . .

RUN rm -rf /root/.gradle/caches

RUN gradle clean build --no-daemon
