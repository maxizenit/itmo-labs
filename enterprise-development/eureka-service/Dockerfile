FROM amazoncorretto:21
ARG JAR_FILE=build/libs/*.jar
ADD ${JAR_FILE} eureka-service.jar
ENTRYPOINT ["java","-jar","/eureka-service.jar"]