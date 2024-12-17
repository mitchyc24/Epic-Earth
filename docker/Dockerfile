FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Create the data directory
RUN mkdir -p /app/data

# Copy the executable JAR file from the build context to the container
COPY ../app/target/epic-earth-0.0.1-SNAPSHOT.jar ./epic-earth.jar

# Copy the .env file from the build context to the container
COPY ../.env ./.env

# Expose the port the application runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "epic-earth.jar"]