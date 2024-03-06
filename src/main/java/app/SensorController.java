package app;

import app.domain.AlertResponse;
import app.domain.MeasurementRequest;
import app.domain.SensorMetricsResponse;
import app.domain.SensorStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sensors")
public class SensorController {

    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/{uuid}/measurements")
    public void collectSensorMeasurement(@PathVariable UUID uuid, @RequestBody MeasurementRequest measurementRequest) {
        sensorService.addMeasurement(uuid, measurementRequest);
    }

    @GetMapping("/{uuid}")
    public SensorStatusResponse getSensorStatus(@PathVariable UUID uuid) {
        return sensorService.getSensorStatus(uuid);
    }

    @GetMapping("/{uuid}/metrics")
    public SensorMetricsResponse getSensorMetrics(@PathVariable UUID uuid) {
        return sensorService.getSensorMetrics(uuid);
    }

    @GetMapping("/{uuid}/alerts")
    public List<AlertResponse> getSensorAlerts(@PathVariable UUID uuid) {
        return sensorService.getSensorAlerts(uuid);
    }
}
