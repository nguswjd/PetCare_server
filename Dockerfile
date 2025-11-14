FROM gradle:7.6.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM azul/zulu-openjdk:17-latest
VOLUME /tmp
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]