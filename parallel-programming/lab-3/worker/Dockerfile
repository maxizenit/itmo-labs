FROM amazoncorretto:21
ARG JAR_FILE=build/libs/*.jar
ADD ${JAR_FILE} worker.jar
ENTRYPOINT ["java","-jar","/worker.jar"]