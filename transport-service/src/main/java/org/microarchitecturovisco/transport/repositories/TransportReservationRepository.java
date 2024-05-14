package org.microarchitecturovisco.transport.repositories;

import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.model.domain.TransportReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransportReservationRepository extends JpaRepository<TransportReservation, Integer> {
    List<TransportReservation> findByTransport(Transport transport);
}