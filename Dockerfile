FROM eclipse-temurin:23-jdk
WORKDIR /app
COPY target/ratelimiter-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]