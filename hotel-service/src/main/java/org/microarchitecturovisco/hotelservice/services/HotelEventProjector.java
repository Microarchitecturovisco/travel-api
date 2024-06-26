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
            else if (hotelEvent instanceof CateringOptionCreatedEvent){
                apply((CateringOptionCreatedEvent) hotelEvent);
            }
            else if (hotelEvent instanceof RoomCreatedEvent){
                apply((RoomCreatedEvent) hotelEvent);
            }
            else if (hotelEvent instanceof RoomReservationCreatedEvent){
                apply((RoomReservationCreatedEvent) hotelEvent);
            }
            else if (hotelEvent instanceof RoomReservationDeletedEvent){
                apply((RoomReservationDeletedEvent) hotelEvent);
            }
            else if (hotelEvent instanceof RoomUpdateEvent) {
                apply((RoomUpdateEvent) hotelEvent);
            }
        }
    }

    private void apply(HotelCreatedEvent event){
        Hotel hotel = new Hotel();

        Location hotelLocation = Location.builder()
                .id(event.getIdLocation())
                .country(event.getCountry())
                .region(event.getRegion())
                .hotel(new ArrayList<Hotel>())
                .build();
        hotelLocation.getHotel().add(hotel);

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
                .hotel(hotel)
                .rating(event.getRating())
                .type(event.getType())
                .build();
        hotel.getCateringOptions().add(cateringOption);
        hotelRepository.save(hotel);

    }

    private void apply(RoomCreatedEvent event){
        Hotel hotel = hotelRepository.findById(event.getIdHotel()).orElseThrow(RuntimeException::new);
        Room room = Room.builder()
                .id(event.getRoomId())
                .name(event.getName())
                .description(event.getDescription())
                .guestCapacity(event.getGuestCapacity())
                .pricePerAdult(event.getPricePerAdult())
                .roomReservations(new ArrayList<>())
                .hotel(hotel)
                .build();

        hotel.getRooms().add(room);
        roomRepository.save(room);
        hotelRepository.save(hotel);

    }

    private void apply(RoomReservationCreatedEvent event){
        Room room = roomRepository.findById(event.getIdRoom()).orElseThrow(RuntimeException::new);
        RoomReservation roomReservation = RoomReservation.builder()
                .id(event.getId())
                .dateFrom(event.getDateFrom())
                .dateTo(event.getDateTo())
                .room(room)
                .mainReservationId(event.getIdRoomReservation())
                .build();
        room.getRoomReservations().add(roomReservation);
        roomReservationRepository.save(roomReservation);
        roomRepository.save(room);


    }
    private void apply(RoomReservationDeletedEvent event) {
        UUID roomId = event.getIdRoom();
        UUID reservationId = event.getIdRoomReservation();

        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null) {
            List<RoomReservation> roomReservations = room.getRoomReservations();

            RoomReservation reservationToDelete = null;
            for (RoomReservation reservation : roomReservations) {
                if (reservation.getMainReservationId().equals(reservationId)) {
                    reservationToDelete = reservation;
                    System.out.println("reservationToDelete:" + reservationToDelete.getMainReservationId());
                    roomReservations.remove(reservationToDelete);
                    roomReservationRepository.delete(reservationToDelete);
                    break;
                }
            }
        }
    }

    private void apply(RoomUpdateEvent event){
        Room room = roomRepository.findById(event.getRoomId()).orElseThrow(RuntimeException::new);

        room.setName(event.getName());
        room.setName(event.getName());
        room.setGuestCapacity(event.getGuestCapacity());
        room.setPricePerAdult(event.getPricePerAdult());
        room.setDescription(event.getDescription());

        roomRepository.save(room);

    }

}
