package org.microarchitecturovisco.hotelservice.repositories;

import org.microarchitecturovisco.hotelservice.model.events.HotelEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomEventStore extends JpaRepository<HotelEvent, UUID> {

    List<HotelEvent> findRoomEventsById(UUID idRoom);
}
