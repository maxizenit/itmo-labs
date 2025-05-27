FROM amazoncorretto:21
ARG JAR_FILE=build/libs/*.jar
ADD ${JAR_FILE} api-gateway-service.jar
ENTRYPOINT ["java","-jar","/api-gateway-service.jar"]