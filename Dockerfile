FROM gradle:7.6.0-jdk17 AS builder

USER root
RUN mkdir -p /home/gradle/.gradle && chown -R gradle:gradle /home/gradle
USER gradle

WORKDIR /app
COPY . .

RUN gradle clean build -x test

FROM azul/zulu-openjdk:17-latest
VOLUME /tmp
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
