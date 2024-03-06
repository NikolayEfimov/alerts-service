package domain.app;

import app.SensorService;
import app.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import app.repositories.AlertRepository;
import app.repositories.MeasurementRepository;
import app.repositories.SensorDataRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static app.domain.SensorStatus.OK;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class SensorServiceTest {

    @Mock
    private SensorDataRepository sensorDataRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private SensorService sensorService;

    private UUID uuid;
    private MeasurementRequest measurementRequest;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        measurementRequest = new MeasurementRequest(2000, LocalDateTime.now());
    }

    @Test
    void testAddMeasurement_NewSensorData() {
        doReturn(Optional.empty()).when(sensorDataRepository).findById(uuid);

        sensorService.addMeasurement(uuid, measurementRequest);

        verify(measurementRepository, times(1)).save(any(Measurement.class));
        verify(sensorDataRepository, times(1)).save(any(SensorData.class));
        verify(alertRepository, never()).save(any());
    }

    @Test
    void testAddMeasurement_AlertConditionMet() {
        var sensorData = new SensorData();
        sensorData.setId(uuid);
        sensorData.setStatus(OK);

        var measurements = new ArrayList<>();
        measurements.add(new Measurement(1L, uuid, 2100, LocalDateTime.now().minusMinutes(1)));
        measurements.add(new Measurement(2L, uuid, 2200, LocalDateTime.now().minusMinutes(2)));
        measurements.add(new Measurement(3L, uuid, 2300, LocalDateTime.now().minusMinutes(3)));

        when(sensorDataRepository.findById(uuid)).thenReturn(Optional.of(sensorData));
        doReturn(measurements).when(measurementRepository).findTop3BySensorIdOrderByTimestampDesc(uuid);

        sensorService.addMeasurement(uuid, measurementRequest);

        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    void testAddMeasurement_AlertConditionNotMet() {
        var sensorData = new SensorData();
        sensorData.setId(uuid);
        sensorData.setStatus(OK);

        var measurements = new ArrayList<>();
        measurements.add(new Measurement(1L, uuid, 2100, LocalDateTime.now().minusMinutes(1)));
        measurements.add(new Measurement(2L, uuid, 2200, LocalDateTime.now().minusMinutes(2)));
        measurements.add(new Measurement(3L, uuid, 1900, LocalDateTime.now().minusMinutes(3)));

        when(sensorDataRepository.findById(uuid)).thenReturn(Optional.of(sensorData));
        doReturn(measurements).when(measurementRepository).findTop3BySensorIdOrderByTimestampDesc(uuid);

        sensorService.addMeasurement(uuid, measurementRequest);

        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void testGetSensorStatus() {
        var sensorData = new SensorData();
        sensorData.setId(uuid);
        sensorData.setStatus(OK);

        when(sensorDataRepository.findById(uuid)).thenReturn(Optional.of(sensorData));

        var expectedResponse = new SensorStatusResponse(OK);
        var actualResponse = sensorService.getSensorStatus(uuid);

        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
    }

    @Test
    void testGetSensorMetrics() {
        var uuid = UUID.randomUUID();
        var thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        var measurements = new ArrayList<>();
        measurements.add(new Measurement(1L, uuid, 1000, thirtyDaysAgo.minusDays(1)));
        measurements.add(new Measurement(2L, uuid, 1500, thirtyDaysAgo.minusDays(2)));
        measurements.add(new Measurement(3L, uuid, 2000, thirtyDaysAgo.minusDays(3)));

        measurements.add(new Measurement(4L, uuid, 3000, thirtyDaysAgo.minusDays(4)));
        measurements.add(new Measurement(5L, uuid, 3500, thirtyDaysAgo.minusDays(5)));
        measurements.add(new Measurement(6L, uuid, 4000, thirtyDaysAgo.minusDays(6)));

        doReturn(measurements).when(measurementRepository).findBySensorIdAndTimestampAfter(eq(uuid), any());

        var actualResponse = sensorService.getSensorMetrics(uuid);

        assertEquals(4000, actualResponse.getMaxLast30Days());
        assertEquals(2500, actualResponse.getAvgLast30Days());
    }

    @Test
    void testGetSensorMetrics_NoMeasurements() {
        var uuid = UUID.randomUUID();

        doReturn(emptyList()).when(measurementRepository).findBySensorIdAndTimestampAfter(eq(uuid), any());

        var actualResponse = sensorService.getSensorMetrics(uuid);

        assertEquals(0, actualResponse.getAvgLast30Days());
        assertEquals(0, actualResponse.getMaxLast30Days());
    }

    @Test
    void testGetSensorAlerts() {
        var uuid = UUID.randomUUID();

        var alerts = new ArrayList<>();
        var startTime = LocalDateTime.now().minusHours(1);
        var endTime = LocalDateTime.now();
        var measurements = new ArrayList<>();
        measurements.add(new Measurement(1L, uuid, 2100, startTime));
        measurements.add(new Measurement(2L, uuid, 2200, startTime.plusMinutes(10)));
        measurements.add(new Measurement(3L, uuid, 2100, startTime.plusMinutes(20)));
        var alert = new Alert(uuid, startTime);
        alert.setFinishTime(endTime);
        alert.setMeasurements(List.of(1L, 2L, 3L));
        alerts.add(alert);

        doReturn(alerts).when(alertRepository).findBySensorIdOrderByStartTimeDesc(uuid);
        doReturn(measurements).when(measurementRepository).findByIdInOrderByTimestamp(List.of(1L, 2L, 3L));

        var actualAlerts = sensorService.getSensorAlerts(uuid);
        var firstAlerts = actualAlerts.getFirst();

        assertEquals(startTime, firstAlerts.getStartTime());
        assertEquals(endTime, firstAlerts.getEndTime());
        assertEquals(2100, firstAlerts.getMeasurement1());
        assertEquals(2200, firstAlerts.getMeasurement2());
        assertEquals(2100, firstAlerts.getMeasurement3());
    }

    @Test
    void testGetSensorAlerts_NoAlerts() {
        var uuid = UUID.randomUUID();

        doReturn(emptyList()).when(alertRepository).findBySensorIdOrderByStartTimeDesc(uuid);

        var expectedResponse = new ArrayList<>();
        var actualResponse = sensorService.getSensorAlerts(uuid);

        assertEquals(expectedResponse, actualResponse);
    }

}

