package cloud.project.datagenerator.hotels.repositories;


import cloud.project.datagenerator.hotels.domain.RoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomReservationRepository extends JpaRepository<RoomReservation, UUID> {
    List<RoomReservation> findByMainReservationId(UUID mainReservationId);
    void deleteByMainReservationId(UUID mainReservationId);
}
