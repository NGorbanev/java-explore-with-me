FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar ewmServiceApp.jar
ENTRYPOINT ["java","-jar","/ewmServiceApp.jar"]