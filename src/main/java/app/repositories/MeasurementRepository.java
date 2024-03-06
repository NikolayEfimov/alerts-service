package app.repositories;

import app.domain.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {

    List<Measurement> findTop3BySensorIdOrderByTimestampDesc(UUID sensorId);

    List<Measurement> findBySensorIdAndTimestampAfter(UUID sensorId, LocalDateTime timestamp);

    List<Measurement> findByIdInOrderByTimestamp(List<Long> measurementIds);
}
