package org.microarchitecturovisco.hotelservice.services;

import lombok.RequiredArgsConstructor;


import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.repositories.RoomEventStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelsCommandService {
    private final RoomEventStore roomEventStore;
    private final HotelEventsProjector hotelEventsProjector;

    public void createReservation(CreateRoomReservationCommand command)
    {
        RoomCreated


    }
    public void createTransport(CreateTransportCommand command) {
        TransportCreatedEvent transportCreatedEvent = new TransportCreatedEvent(
                command.getUuid(), command.getCommandTimeStamp(), command.getTransportDto()
        );

        transportEventStore.save(transportCreatedEvent);
        eventSourcingHandler.project(List.of(transportCreatedEvent));
    }

    public void createReservation(CreateTransportReservationCommand command) {
        TransportReservationCreatedEvent transportReservationCreatedEvent = TransportReservationCreatedEvent.builder()
                .id(command.getUuid())
                .eventTimeStamp(command.getCommandTimeStamp())
                .idTransportReservation(command.getTransportReservationDto().getIdTransportReservation())
                .idTransport(command.getTransportReservationDto().getIdTransport())
                .build();

        transportEventStore.save(transportReservationCreatedEvent);
        eventSourcingHandler.project(List.of(transportReservationCreatedEvent));
    }
}
