package app.repositories;

import app.domain.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, UUID> {
}

