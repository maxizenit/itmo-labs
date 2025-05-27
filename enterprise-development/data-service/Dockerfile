FROM amazoncorretto:21
ARG JAR_FILE=build/libs/*.jar
ADD ${JAR_FILE} data-service.jar
ENTRYPOINT ["java","-jar","/data-service.jar"]