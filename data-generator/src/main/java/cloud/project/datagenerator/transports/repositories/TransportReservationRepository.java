package cloud.project.datagenerator.transports.repositories;

import cloud.project.datagenerator.transports.domain.TransportReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransportReservationRepository extends JpaRepository<TransportReservation, UUID> {

    void deleteByMainReservationId(UUID id);
}
