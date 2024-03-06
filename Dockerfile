FROM gradle:8.4-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:21-slim

ARG JAR_FILE=target/*.jar

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/alerts.jar

ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/app/alerts.jar"]