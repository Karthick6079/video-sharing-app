#FROM maven:3.9.6-eclipse-temurin-17 AS build-stage
#WORKDIR /workspace
#COPY . .
#RUN mvn clean package -DskipTests

#FROM eclipse-temurin:17-jdk-alpine
#COPY --from=build-stage /workspace/target/*.jar app.jar
#CMD ["java","-jar","app.jar"]
#EXPOSE 8081

# Stage 1: Copy only pom.xml and download dependencies
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only dependency descriptor to cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

VOLUME /logs
# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=cloud
CMD ["java", "-jar", "app.jar"]
EXPOSE 8081

