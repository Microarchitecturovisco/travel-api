package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.model.events.HotelEvent;
import org.microarchitecturovisco.hotelservice.model.events.RoomReservationCreatedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HotelEventStore extends JpaRepository<HotelEvent, UUID> {
}
