# Weather API

## Overview

This project is a Weather API for displaying city temperatures. The application has a Java/Spring backend with a Swagger REST API. It fetches and updates the current temperature for cities from the OpenWeatherMap API.

## Features

- Loads 100 city names from a CSV file into the database on startup.
- Fetches current temperatures for all cities and updates the database.
- REST API to see results, delete all results, and query new temperatures.
- REST API GET queries support sorting and paging.
- Automated scheduled task to update weather data daily (optional).
- Dockerfile to run the application as a Docker container.

## Requirements

- JDK 11 or later
- Gradle
- Docker (optional)

## Setup

### Configuration

1. Add your OpenWeatherMap API key in the `application.yml` file located in `src/main/resources`.

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password: password
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

weather:
  api:
    key: your_api_key_here
  executor:
    thread-pool-size: 10  # Number of threads
```
# Building and Running
## Using Gradle
### Build the project:

```sh
./gradlew build
```
### Run the project:

```sh
./gradlew bootRun
```
### Access the application:
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb, User Name: sa, Password: password)

## Using Docker

###  Dockerfile

```dockerfile
# Use the official OpenJDK base image
FROM openjdk:11-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the build files
COPY build/libs/weather-api-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build the Docker image:

```sh
docker build -t weather-api .
```

## Run the Docker container:

```sh
docker run -p 8080:8080 weather-api
```

## API Endpoints

### Get list of cities with pagination

**Endpoint:** `GET /api/cities`

**Description:** Retrieves a list of cities with pagination.

**Parameters:**

- `page` - Page number (0-based index)
- `size` - Number of items per page

### Get temperature in Celsius by city ID

**Endpoint:** `GET /api/cities/{id}/celsius`

**Description:** Retrieves the temperature in Celsius for a city by its OpenWeatherMap ID.

**Parameters:**

- `id` - City ID

### Get temperature in Fahrenheit by city ID

**Endpoint:** `GET /api/cities/{id}/fahrenheit`

**Description:** Retrieves the temperature in Fahrenheit for a city by its OpenWeatherMap ID.

**Parameters:**

- `id` - City ID

### Get temperature in Celsius by city name

**Endpoint:** `GET /api/cities/name/{name}/celsius`

**Description:** Retrieves the temperature in Celsius for a city by its name.

**Parameters:**

- `name` - City name

### Get temperature in Fahrenheit by city name

**Endpoint:** `GET /api/cities/name/{name}/fahrenheit`

**Description:** Retrieves the temperature in Fahrenheit for a city by its name.

**Parameters:**

- `name` - City name

### Delete all cities

**Endpoint:** `DELETE /api/cities`

**Description:** Deletes all cities from the database.

### Update weather data for all cities

**Endpoint:** `POST /api/cities/update`

**Description:** Updates the weather data for all cities in the database.
