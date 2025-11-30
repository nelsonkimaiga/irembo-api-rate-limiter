# Distributed API Rate Limiter

## Overview
This is a backend service that enforces per-client rate limiting (10 requests per minute) based on various use-cases

## Stack
1. Java 17
2. SpringBoot Framework
3. Redis

## Getting Started
This project requires Docker and Docker Compose to run the complete distributed stack.

Execute the following commands from the root directory to build the Spring Boot application, deploy two API instances, and start the Redis container:

#### Ensure any old containers are removed
`docker-compose down`

#### Build the JAR, build the image
`docker-compose up --build -d`

2. Access the Application
The two API instances are exposed on ports 8080 and 8081 via the Docker Compose file.

Frontend: Access the web interface at `http://localhost:4200` if running the Angular dev server locally.

API Endpoint: `http://localhost:8080/api/notify/send`

### Testing the Distributed Rate Limiter

Required Header: All requests must include the following header to be processed:

`X-API-KEY`: `CLIENT-API-KEY-123`

### Test Scenarios via the Web Interface

1. Open the web application and click the "Send Notification Request" button rapidly.
2. The limit is set to 10 requests per minute.
