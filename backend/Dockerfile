FROM maven:3.9.6 AS build-stage
WORKDIR /workspace
COPY pom.xml ./
RUN mvn dependency:resolve
COPY . .
RUN mvn package -DskipTests  -X
EXPOSE 8080
FROM openjdk:17-jdk-slim
COPY --from=build-stage /workspace/target/*.jar app.jar
CMD ["java","-jar","app.jar"]
