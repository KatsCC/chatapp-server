FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

RUN apt-get update && apt-get install -y wget unzip

RUN wget https://services.gradle.org/distributions/gradle-8.2.1-bin.zip && \
    unzip gradle-8.2.1-bin.zip -d /opt/gradle && \
    rm gradle-8.2.1-bin.zip

ENV GRADLE_HOME=/opt/gradle/gradle-8.2.1
ENV PATH="${GRADLE_HOME}/bin:${PATH}"

COPY . .
RUN ./gradlew clean build --no-daemon

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/chatapp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dserver.port=$PORT", "-jar", "/app/app.jar"]
