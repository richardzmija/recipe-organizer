# Build stage
# Use an image with Maven and JDK 21 to build the application.
FROM maven:3.9-eclipse-temurin-21-alpine AS build
# Create and set the working directory to /app.
WORKDIR /app
# Copy the pom.xml before the source code to enable layer caching
# if the dependencies don't change.
COPY pom.xml .
# Download the project dependencies before copying the source code by
# running maven in non-interactive mode. We do this before copying
# the source code to improve build caching (if only the code changes, the
# dependencies won't need to be redownloaded).
RUN mvn dependency:go-offline -B || true
# Copy the application source to to the container filesystem.
COPY src ./src
# Compile the application and package it into a JAR file.
RUN mvn package -DskipTests

# Runtime stage
# Use a smaller image with JRE 21 to run the application.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy just the JAR file that we built in the previous stage.
COPY --from=build /app/target/*.jar app.jar
# This is for documentation that the application listens on port 8080.
EXPOSE 8080
# This is the command that will be run when the container starts.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
