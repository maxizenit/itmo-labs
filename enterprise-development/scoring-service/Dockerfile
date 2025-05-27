FROM amazoncorretto:21
ARG JAR_FILE=build/libs/*.jar
ADD ${JAR_FILE} scoring-service.jar
ENTRYPOINT ["java","-jar","/scoring-service.jar"]