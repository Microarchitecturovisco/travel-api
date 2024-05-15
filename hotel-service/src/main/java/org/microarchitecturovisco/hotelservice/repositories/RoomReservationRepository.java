package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.model.domain.RoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomReservationRepository extends JpaRepository<RoomReservation, UUID> {
}
