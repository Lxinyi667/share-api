FROM openjdk:17

ARG JAR_FILE=./share-user/target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]