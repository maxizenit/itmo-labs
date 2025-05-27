FROM amazoncorretto:21
ARG JAR_FILE=build/libs/*.jar
ADD ${JAR_FILE} report-service.jar
ENTRYPOINT ["java","-jar","/report-service.jar"]