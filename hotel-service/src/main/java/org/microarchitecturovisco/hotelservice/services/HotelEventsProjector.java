package org.microarchitecturovisco.hotelservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.model.domain.Room;
import org.microarchitecturovisco.hotelservice.model.domain.RoomReservation;
import org.microarchitecturovisco.hotelservice.model.events.HotelEvent;
import org.microarchitecturovisco.hotelservice.model.events.RoomReservationCreatedEvent;
import org.microarchitecturovisco.hotelservice.repositories.RoomEventStore;
import org.microarchitecturovisco.hotelservice.repositories.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HotelEventsProjector {

    private final RoomEventStore eventStore;

    private final RoomRepository roomRepository;

    public void project(UUID idRoom){
        List<HotelEvent> hotelEvents = eventStore.findRoomEventsById(idRoom);

        Room room = new Room();

        for (HotelEvent hotelEvent : hotelEvents) {
            if (hotelEvent instanceof RoomReservationCreatedEvent){
                apply((RoomReservationCreatedEvent) hotelEvent, room);
            }
        }
    }

    private void apply(RoomReservationCreatedEvent event, Room room){
        RoomReservation reservation = RoomReservation.builder()
                .id(event.getId())
                .dateFrom(event.getDateFrom())
                .dateTo(event.getDateTo())
                .room(event.getRoom()) //tu cos trzeba zmienc
                .build();
        room.getRoomReservations().add(reservation);

        roomRepository.save(room); ///to tez cos nietak
    }
}
