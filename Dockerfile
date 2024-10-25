# Use a suitable JDK image for building
FROM openjdk:17-jdk-slim as builder

# Set working directory
WORKDIR /app

# Copy Gradle wrapper, configuration, and source files
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts ./
COPY settings.gradle.kts ./
COPY src ./src

# Build the jar file
RUN ./gradlew build -x test

# Use a Flink base image for running the job
FROM flink:latest

# Set the working directory
WORKDIR /flink-job

# Copy the built jar from the builder image
COPY --from=builder /app/build/libs/CarFlink-1.0-SNAPSHOT.jar /flink-job/CarFlink.jar

# Entry point to run the job
CMD ["bin/flink", "run", "-d", "/flink-job/CarFlink.jar"]
