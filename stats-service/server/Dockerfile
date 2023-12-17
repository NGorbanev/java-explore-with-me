FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar statsServer.jar
ENTRYPOINT ["java","-jar","/statsServer.jar"]