package org.microarchitecturovisco.hotelservice.services;

import lombok.RequiredArgsConstructor;


import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateHotelCommand;
import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.model.dto.CateringOptionDto;
import org.microarchitecturovisco.hotelservice.model.dto.RoomDto;
import org.microarchitecturovisco.hotelservice.model.events.CateringOptionCreatedEvent;
import org.microarchitecturovisco.hotelservice.model.events.HotelCreatedEvent;
import org.microarchitecturovisco.hotelservice.model.events.RoomCreatedEvent;
import org.microarchitecturovisco.hotelservice.model.events.RoomReservationCreatedEvent;
import org.microarchitecturovisco.hotelservice.repositories.HotelEventStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HotelsCommandService {
    private final HotelEventStore hotelEventStore;
    private final HotelEventProjector hotelEventProjector;


    public void createHotel(CreateHotelCommand command) {
        HotelCreatedEvent hotelCreatedEvent =  new HotelCreatedEvent(command.getCommandTimeStamp(),
                command.getHotelDto());
        hotelCreatedEvent.setId(UUID.randomUUID());
        hotelEventStore.save(hotelCreatedEvent);
        hotelEventProjector.project(List.of(hotelCreatedEvent));

        for (RoomDto roomDto : command.getHotelDto().getRooms()){
            RoomCreatedEvent roomCreatedEvent = new RoomCreatedEvent(command.getCommandTimeStamp(),
                    roomDto, command.getHotelDto().getHotelId());
            roomCreatedEvent.setId(UUID.randomUUID());
            hotelEventStore.save(roomCreatedEvent);
            hotelEventProjector.project(List.of(roomCreatedEvent));
        }
        for (CateringOptionDto cateringOptionDto : command.getHotelDto().getCateringOptions()){
            CateringOptionCreatedEvent cateringOptionCreatedEvent = CateringOptionCreatedEvent.builder()
                    .id(UUID.randomUUID())
                    .idHotel(command.getHotelDto().getHotelId())
                    .eventTimeStamp(command.getCommandTimeStamp())
                    .idCatering(cateringOptionDto.getCateringId())
                    .type(cateringOptionDto.getType())
                    .rating(cateringOptionDto.getRating())
                    .price(cateringOptionDto.getPrice())
                    .build();
            hotelEventStore.save(cateringOptionCreatedEvent);
            hotelEventProjector.project(List.of(cateringOptionCreatedEvent));
        }
    }

    public void createReservation(CreateRoomReservationCommand command){
        RoomReservationCreatedEvent reservationCreatedEvent =  RoomReservationCreatedEvent.builder()
                .eventTimeStamp(command.getCommandTimeStamp())
                .dateFrom(command.getRoomReservationDto().getDateFrom())
                .dateTo(command.getRoomReservationDto().getDateTo())
                .idRoomReservation(command.getRoomReservationDto().getReservationId())
                .idHotel(command.getHotelId())
                .idRoom(command.getRoomId())
                .build();
        reservationCreatedEvent.setId(UUID.randomUUID());
        hotelEventStore.save(reservationCreatedEvent);
        hotelEventProjector.project(List.of(reservationCreatedEvent));
    }

}
