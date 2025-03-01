FROM maven:3.9.6 AS build-stage
WORKDIR /workspace
COPY pom.xml ./
RUN mvn dependency:resolve
COPY . .
RUN mvn package -DskipTests

FROM openjdk:17-alpine
COPY --from=build-stage /workspace/target/*.jar app.jar
CMD ["java","-jar","app.jar"]
EXPOSE 8081

