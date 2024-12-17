# filepath: /c:/Users/mitch/OneDrive/Documents/Personal/Education/WLU/Courses/CP630-Enterprise_Computing/enterprise/workspace/project/EPIC_Earth/docker/Dockerfile
# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file from the build context to the container
COPY ../app/target/epic-earth-0.0.1-SNAPSHOT.jar ./epic-earth.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "epic-earth.jar"]