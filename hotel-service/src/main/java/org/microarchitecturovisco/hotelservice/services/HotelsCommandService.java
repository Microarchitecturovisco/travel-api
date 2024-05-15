package org.microarchitecturovisco.hotelservice.services;

import lombok.RequiredArgsConstructor;


import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.model.events.RoomReservationAddEvent;
import org.microarchitecturovisco.hotelservice.repositories.RoomEventStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelsCommandService {
    private final RoomEventStore roomEventStore;
    private final RoomEventsProjector roomEventsProjector;

    public void createReservation(CreateRoomReservationCommand command)
    {
        RoomReservationAddEvent roomReservationAddEvent = RoomReservationAddEvent.builder()
                .roomId()
    }
}
