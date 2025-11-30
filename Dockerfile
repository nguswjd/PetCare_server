FROM eclipse-temurin:21-jdk-alpine
ARG JAR_PATH=./build/libs
COPY ${JAR_PATH}/petcare-0.0.1-SNAPSHOT.jar ${JAR_PATH}/petcare-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","./build/libs/petcare-0.0.1-SNAPSHOT.jar"]