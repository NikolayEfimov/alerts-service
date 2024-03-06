# CO2 Sensor Service

This service collects data from CO2 sensors and provides functionality to monitor CO2 levels, receive alerts, and
retrieve sensor metrics.

## Requirements

- Java 8 or higher
- Gradle 8 
- Postgres

## Building the Project

To build the project, run the following command in the project root directory:

```
./gradlew build
```

# Run

Start service via docker is almost done but not properly tested, so better to do it manually.

## Prepare db

- Download Postgres
- execute init script /resources/init.sql

## Running the Service

To run the service, use the following command:

```
./gradlew bootRun
```

By default, the service will run on `http://localhost:8080`.


Testing
Unit tests and integration tests are included in the src/test directory. You can run the tests using:

```
./gradlew test
```