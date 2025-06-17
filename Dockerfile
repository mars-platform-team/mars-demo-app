# Use an official OpenJDK runtime as a parent image
FROM <internal image registry>/openjdk:21-jdk-slim
USER 1000
# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file
COPY build/libs/mars-demo-app.jar /app/mars-demo-app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/mars-demo-app.jar"]