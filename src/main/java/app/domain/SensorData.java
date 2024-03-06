package app.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(schema = "sensor_data", name = "sensor_data")
public class SensorData {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SensorStatus status;

    public SensorStatus getStatus() {
        return status;
    }

    public void setStatus(SensorStatus status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}



