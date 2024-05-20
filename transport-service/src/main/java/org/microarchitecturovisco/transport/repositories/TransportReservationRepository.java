package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.TransportReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransportReservationRepository extends JpaRepository<TransportReservation, UUID> {

    void deleteByMainReservationId(UUID id);
}
