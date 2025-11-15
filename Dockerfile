FROM gradle:7.6.0-jdk17 AS builder
WORKDIR /app

USER root
RUN mkdir -p /home/gradle/.gradle && chown -R gradle:gradle /home/gradle/.gradle

COPY . .

USER gradle
RUN gradle clean build -x test

FROM azul/zulu-openjdk:17-latest
VOLUME /tmp
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
