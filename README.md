# CO2 Sensor Service

This service collects data from CO2 sensors and provides functionality to monitor CO2 levels, receive alerts, and
retrieve sensor metrics.

## Requirements

- Java 8 or higher
- Gradle

## Building the Project

To build the project, run the following command in the project root directory:

```
./gradlew build
```

## Running the Service

To run the service, use the following command:

```
./gradlew bootRun
```

By default, the service will run on `http://localhost:8080`.

## API Documentation

### Collect Sensor Measurements

**POST** `/api/v1/sensors/{uuid}/measurements`

```json
{
  "co2": 2000,
  "time": "2019-02-01T18:55:47+00:00"
}
```

Get Sensor Status
**GET** `/api/v1/sensors/{uuid}`

Response:

```json
{
  "status": "OK"
}
```

Get Sensor Metrics
**GET** `/api/v1/sensors/{uuid}/metrics`

Response:

```json
{
  "maxLast30Days": 1200,
  "avgLast30Days": 900
}
```

List Alerts for a Sensor
**GET** `/api/v1/sensors/{uuid}/alerts`

Response:

```json
[
  {
    "startTime": "2019-02-02T18:55:47+00:00",
    "endTime": "2019-02-02T20:00:47+00:00",
    "measurement1": 2100,
    "measurement2": 2200,
    "measurement3": 2100
  }
]
```

Testing
Unit tests and integration tests are included in the src/test directory. You can run the tests using:

```
./gradlew test
```