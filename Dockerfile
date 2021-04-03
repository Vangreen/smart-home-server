#x86
#FROM openjdk:11-jre-slim
#arm
FROM balenalib/raspberrypi3-openjdk:8-jdk
EXPOSE 9999
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
