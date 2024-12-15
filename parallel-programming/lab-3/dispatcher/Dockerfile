FROM amazoncorretto:21
ARG JAR_FILE=build/libs/*.jar
ADD ${JAR_FILE} dispatcher.jar
ENTRYPOINT ["java","-jar","/dispatcher.jar"]