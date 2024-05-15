package org.microarchitecturovisco.hotelservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.model.domain.*;
import org.microarchitecturovisco.hotelservice.model.events.*;
import org.microarchitecturovisco.hotelservice.repositories.HotelEventStore;
import org.microarchitecturovisco.hotelservice.repositories.HotelRepository;
import org.microarchitecturovisco.hotelservice.repositories.RoomRepository;
import org.microarchitecturovisco.hotelservice.repositories.RoomReservationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HotelEventProjector {

    private final HotelEventStore eventStore;

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomReservationRepository roomReservationRepository;

    public void project(List<HotelEvent> hotelEvents){
        for (HotelEvent hotelEvent : hotelEvents) {
            if (hotelEvent instanceof HotelCreatedEvent){
                apply((HotelCreatedEvent) hotelEvent);
            }
            if (hotelEvent instanceof CateringOptionCreatedEvent){
                apply((CateringOptionCreatedEvent) hotelEvent);
            }
        }
    }

    private void apply(HotelCreatedEvent event){
        Hotel hotel = new Hotel();

        Location hotelLocation = Location.builder()
                .id(event.getIdLocation())
                .country(event.getCountry())
                .region(event.getRegion())
                .build();


        hotel.setLocation(hotelLocation);
        hotel.setId(event.getIdHotel());
        hotel.setName(event.getName());
        hotel.setDescription(event.getDescription());
        hotel.setPhotos(event.getPhotos());
        hotel.setRating(event.getRating());
        hotel.setCateringOptions(new ArrayList<>());
        hotel.setRooms(new ArrayList<>());
        hotelRepository.save(hotel);
    }

    private void apply(CateringOptionCreatedEvent event){
        Hotel hotel = hotelRepository.findById(event.getIdHotel()).orElseThrow(RuntimeException::new);

        CateringOption cateringOption = CateringOption.builder()
                .id(event.getIdCatering())
                .price(event.getPrice())
                .rating(event.getRating())
                .type(event.getType())
                .build();
        hotel.getCateringOptions().add(cateringOption);
        hotelRepository.save(hotel);

    }

    private void apply(RoomCreatedEvent event){
        Room room = Room.builder()
                .id(event.getRoomId())
                .name(event.getName())
                .description(event.getDescription())
                .guestCapacity(event.getGuestCapacity())
                .pricePerAdult(event.getPricePerAdult())
                .roomReservations(new ArrayList<>())
                .build();

        Hotel hotel = hotelRepository.findById(event.getIdHotel()).orElseThrow(RuntimeException::new);
        hotel.getRooms().add(room);
        roomRepository.save(room);
        hotelRepository.save(hotel);

    }

    private void apply(RoomReservationCreatedEvent event){
        RoomReservation roomReservation = RoomReservation.builder()
                .id(event.getId())
                .dateFrom(event.getDateFrom())
                .dateTo(event.getDateTo())
                .build();

        Room room = roomRepository.findById(event.getIdRoom()).orElseThrow(RuntimeException::new);
        room.getRoomReservations().add(roomReservation);
        roomReservationRepository.save(roomReservation);
        roomRepository.save(room);


    }

}
