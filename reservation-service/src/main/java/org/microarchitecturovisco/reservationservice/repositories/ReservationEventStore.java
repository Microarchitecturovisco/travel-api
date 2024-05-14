package org.microarchitecturovisco.reservationservice.repositories;

import org.microarchitecturovisco.reservationservice.domain.events.ReservationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationEventStore extends JpaRepository<ReservationEvent, String> {
}
