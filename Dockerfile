# Use a base image with Java
FROM eclipse-temurin:23-jdk

# Set working dir
WORKDIR /app

# Copy jar from build context
COPY target/ratelimiter-0.0.1-SNAPSHOT.jar app.jar

# Expose port (match your app's port)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]