package app.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(schema = "alerts", name = "alert")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id")
    private UUID sensorId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "finish_time")
    private LocalDateTime finishTime;

    @ElementCollection
    @CollectionTable(name = "alert_measurement", joinColumns = @JoinColumn(name = "alert_id"))
    @Column(name = "measurement_id")
    private List<Long> measurements;

    public Alert() {}

    public Alert(UUID sensorId, LocalDateTime startTime) {
        this.sensorId = sensorId;
        this.startTime = startTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public List<Long> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Long> measurements) {
        this.measurements = measurements;
    }
}

