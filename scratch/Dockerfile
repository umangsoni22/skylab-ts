# Use the official OpenJDK base image
FROM openjdk:latest

# Set the working directory inside the container
WORKDIR /app

# Copy the Java application JAR into the container
COPY "skylab-ts-1.0-SNAPSHOT.jar" /app/OpenTelemetrySample.jar

# Run the Java application when the container starts
CMD ["java", "-jar", "OpenTelemetrySample.jar"]
