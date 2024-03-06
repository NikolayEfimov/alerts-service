package app.repositories;

import app.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    @Query("SELECT a FROM Alert a WHERE a.sensorId = :sensorId ORDER BY a.startTime DESC")
    List<Alert> findBySensorIdOrderByStartTimeDesc(UUID sensorId);

    Alert findTop1BySensorIdOrderByStartTimeDesc(UUID sensorId);
}
