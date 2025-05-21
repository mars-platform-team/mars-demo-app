# Use an official OpenJDK runtime as a parent image
FROM docker.artifactory.mars.pcf-maximus.com/openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file
COPY app/build/libs/app.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]