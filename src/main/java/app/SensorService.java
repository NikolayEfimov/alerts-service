package app;

import app.domain.*;
import app.repositories.AlertRepository;
import app.repositories.MeasurementRepository;
import app.repositories.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static app.domain.SensorStatus.*;

@Service
public class SensorService {

    private final SensorDataRepository sensorDataRepository;
    private final MeasurementRepository measurementRepository;
    private final AlertRepository alertRepository;

    private static final int MEASUREMENT_ALERT_LEVEL = 2000;

    @Autowired
    public SensorService(SensorDataRepository sensorDataRepository, MeasurementRepository measurementRepository, AlertRepository alertRepository) {
        this.sensorDataRepository = sensorDataRepository;
        this.measurementRepository = measurementRepository;
        this.alertRepository = alertRepository;
    }

    public void addMeasurement(UUID uuid, MeasurementRequest measurementRequest) {
        sensorDataRepository.findById(uuid).orElseGet(() -> {
            var newSensorData = new SensorData();
            newSensorData.setId(uuid);
            newSensorData.setStatus(OK);
            return sensorDataRepository.save(newSensorData);
        });

        var measurement = new Measurement();
        var lastMeasurement = measurementRequest.getCo2();
        measurement.setSensorId(uuid);
        measurement.setValue(lastMeasurement);
        measurement.setTimestamp(measurementRequest.getTime());

        measurementRepository.save(measurement);

        checkForAlerts(uuid, lastMeasurement);
    }

    public SensorStatusResponse getSensorStatus(UUID uuid) {
        var sensorData = sensorDataRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        return new SensorStatusResponse(sensorData.getStatus());
    }

    public SensorMetricsResponse getSensorMetrics(UUID uuid) {
        return new SensorMetricsResponse(findMaxCO2Level(uuid), calculateAverageCO2Level(uuid));
    }

    private int calculateAverageCO2Level(UUID uuid) {
        var thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        var measurements = measurementRepository.findBySensorIdAndTimestampAfter(uuid, thirtyDaysAgo);

        double averageCO2Level = measurements.stream()
                .mapToInt(Measurement::getValue)
                .average()
                .orElse(0.0);

        return (int) averageCO2Level;
    }

    private int findMaxCO2Level(UUID uuid) {
        var thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        var measurements = measurementRepository.findBySensorIdAndTimestampAfter(uuid, thirtyDaysAgo);

        return measurements.stream()
                .mapToInt(Measurement::getValue)
                .max()
                .orElse(0);
    }

    public List<AlertResponse> getSensorAlerts(UUID uuid) {
        var alerts = alertRepository.findBySensorIdOrderByStartTimeDesc(uuid);
        return alerts.stream()
                .map(this::mapToAlertResponse)
                .collect(Collectors.toList());
    }

    private AlertResponse mapToAlertResponse(Alert alert) {
        var alertResponse = new AlertResponse();
        alertResponse.setStartTime(alert.getStartTime());
        alertResponse.setEndTime(alert.getFinishTime());
        var measurementsIds = alert.getMeasurements();

        var measurements = measurementRepository.findByIdInOrderByTimestamp(measurementsIds);
        if (measurements != null && measurements.size() >= 3) {
            alertResponse.setMeasurement1(measurements.get(0).getValue());
            alertResponse.setMeasurement2(measurements.get(1).getValue());
            alertResponse.setMeasurement3(measurements.get(2).getValue());
        }
        return alertResponse;
    }

    void checkForAlerts(UUID uuid, int lastMeasurement) {
        var latestMeasurements = measurementRepository.findTop3BySensorIdOrderByTimestampDesc(uuid);

        if (latestMeasurements.size() < 3) {
            return;
        }

        int countAboveThreshold = (int) latestMeasurements.stream()
                .filter(measurement -> measurement.getValue() > MEASUREMENT_ALERT_LEVEL)
                .count();

        var sensorData = sensorDataRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        if (countAboveThreshold >= 3) {
            sensorData.setStatus(ALERT);
            // Add the latest measurements to the list
            var measurements = new ArrayList<>(latestMeasurements);
            var ids = measurements.stream()
                    .map(Measurement::getId)
                    .toList();
            // Create an alert and associate the measurements with it
            var alert = new Alert(uuid, LocalDateTime.now());
            alert.setMeasurements(ids);

            alertRepository.save(alert);
        } else if (lastMeasurement > MEASUREMENT_ALERT_LEVEL) {
            sensorData.setStatus(WARN);
        } else if (countAboveThreshold == 0) {
            if (sensorData.getStatus() == ALERT) {
               var alert = alertRepository.findTop1BySensorIdOrderByStartTimeDesc(uuid);
               alert.setFinishTime(LocalDateTime.now());
               alertRepository.save(alert);
            }
            sensorData.setStatus(OK); // Since the transition from WARN to OK is not clear in the requirement, let's say we go to OK from WARN immediately after good measurement.
        }

        sensorDataRepository.save(sensorData);
    }
}

