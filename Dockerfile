FROM openjdk:11.0.6-jre-buster
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} v1-1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/v1-1-SNAPSHOT.jar"]