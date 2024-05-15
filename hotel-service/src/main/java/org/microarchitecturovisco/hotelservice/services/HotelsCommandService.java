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

@Service
@RequiredArgsConstructor
public class HotelsCommandService {
    private final HotelEventStore hotelEventStore;
    private final HotelEventProjector hotelEventProjector;


    public void createHotel(CreateHotelCommand command) {
        HotelCreatedEvent hotelCreatedEvent =  new HotelCreatedEvent(command.getCommandTimeStamp(),
                command.getHotelDto());
        hotelEventStore.save(hotelCreatedEvent);
        hotelEventProjector.project(List.of(hotelCreatedEvent));

        for (RoomDto roomDto : command.getHotelDto().getRooms()){
            RoomCreatedEvent roomCreatedEvent = new RoomCreatedEvent(command.getCommandTimeStamp(),
                    roomDto, command.getHotelDto().getHotelId());
            hotelEventStore.save(roomCreatedEvent);
            hotelEventProjector.project(List.of(roomCreatedEvent));
        }
        for (CateringOptionDto cateringOptionDto : command.getHotelDto().getCateringOptions()){
            CateringOptionCreatedEvent cateringOptionCreatedEvent = CateringOptionCreatedEvent.builder()
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
        hotelEventStore.save(reservationCreatedEvent);
        hotelEventProjector.project(List.of(reservationCreatedEvent));
    }

}
