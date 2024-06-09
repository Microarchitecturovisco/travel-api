package org.microarchitecturovisco.hotelservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.model.domain.*;

import org.microarchitecturovisco.hotelservice.model.dto.RoomsConfigurationDto;
import org.microarchitecturovisco.hotelservice.model.dto.request.*;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelsBySearchQueryResponseDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelDetailsResponseDto;
import org.microarchitecturovisco.hotelservice.model.events.RoomCreatedEvent;
import org.microarchitecturovisco.hotelservice.model.events.RoomUpdateEvent;
import org.microarchitecturovisco.hotelservice.model.exceptions.HotelNoFoundException;
import org.microarchitecturovisco.hotelservice.model.mappers.CateringMapper;
import org.microarchitecturovisco.hotelservice.model.mappers.HotelMapper;
import org.microarchitecturovisco.hotelservice.model.mappers.LocationMapper;
import org.microarchitecturovisco.hotelservice.model.mappers.RoomMapper;
import org.microarchitecturovisco.hotelservice.repositories.HotelRepository;
import org.microarchitecturovisco.hotelservice.repositories.RoomRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelsService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final HotelEventProjector hotelEventProjector;

    public GetHotelDetailsResponseDto getHotelDetails(GetHotelDetailsRequestDto requestDto){
        LocalDateTime dateFrom = requestDto.getDateFrom();
        LocalDateTime dateTo = requestDto.getDateTo();

        Hotel hotel = hotelRepository.findById(requestDto.getHotelId()).orElseThrow();

        GetHotelDetailsResponseDto responseDto = GetHotelDetailsResponseDto.builder()
                .hotelId(hotel.getId())
                .description(hotel.getDescription())
                .rating(hotel.getRating())
                .hotelName(hotel.getName())
                .photos(hotel.getPhotos())
                .location(LocationMapper.map(hotel.getLocation()))
                .cateringOptions(CateringMapper.mapList(hotel.getCateringOptions()))
                .roomsConfigurations(new ArrayList<>())
                .build();

        int numberOfGuests = requestDto.getAdults() + requestDto.getChildrenUnderEighteen() + requestDto.getChildrenUnderTen() + requestDto.getChildrenUnderThree();
        List<Room> hotelRooms = roomRepository.findAvailableRoomsByHotelAndDate(requestDto.getHotelId(), dateFrom, dateTo);
        int HOTEL_CONFIGURATION_NUMBER = 3;
        for (int i = 0; i< HOTEL_CONFIGURATION_NUMBER; i++)
        {
            Pair<List<Room>, Float> pair = getRoomConfigurationForAmountOfPeople(hotelRooms, numberOfGuests);
            if (pair.getSecond() != 0) {
                RoomsConfigurationDto roomsConfigurationDto = RoomsConfigurationDto.builder()
                        .rooms(RoomMapper.mapList(pair.getFirst()))
                        .pricePerAdult(pair.getSecond())
                        .build();
                responseDto.getRoomsConfigurations().add(roomsConfigurationDto);
                hotelRooms.removeAll(pair.getFirst());
            }
        }
        return responseDto;

    }

    public Pair<List<Room>, Float> getRoomConfigurationForAmountOfPeople(List<Room> rooms, int numberOfPeople){
        List<Room> roomConfiguration = new ArrayList<>();
        List<Room> sortedRooms = new ArrayList<>(rooms.stream()
                .sorted(Comparator.comparingInt(Room::getGuestCapacity))
                .toList());
        float currentPrice = 0;
        int currentPeople = 0;
        while (numberOfPeople > 0) {
            for (int i = 0; i < sortedRooms.size(); i++) {
                if (sortedRooms.get(i).getGuestCapacity() >= numberOfPeople || i == sortedRooms.size() - 1) {
                    numberOfPeople-= sortedRooms.get(i).getGuestCapacity();
                    currentPrice = ((currentPrice * currentPeople) + (sortedRooms.get(i).getPricePerAdult() * sortedRooms.get(i).getGuestCapacity()))
                            / (currentPeople + sortedRooms.get(i).getGuestCapacity());
                    currentPeople += sortedRooms.get(i).getGuestCapacity();
                    roomConfiguration.add(sortedRooms.get(i));
                    sortedRooms.remove(i);

                    break;
                }
            }
            if (sortedRooms.isEmpty()) {break;}
        }
        if (numberOfPeople <= 0) {
            return Pair.of(roomConfiguration, currentPrice);
        }
        currentPrice = 0;
        return Pair.of(new ArrayList<>(), currentPrice);
    }


    public GetHotelsBySearchQueryResponseDto GetHotelsBySearchQuery(GetHotelsBySearchQueryRequestDto requestDto) {
        LocalDateTime dateFrom = requestDto.getDateFrom();
        LocalDateTime dateTo = requestDto.getDateTo();

        List<Hotel> availableHotels = new ArrayList<>();
        List<Float> pricesPerAdult = new ArrayList<>();

        List<UUID> arrivalLocationIds = requestDto.getArrivalLocationIds();

        List<Room> availableRooms = roomRepository.findAvailableRoomsByLocationAndDate(arrivalLocationIds, dateFrom, dateTo);

        int numberOfGuests = requestDto.getAdults() + requestDto.getChildrenUnderEighteen() + requestDto.getChildrenUnderTen() + requestDto.getChildrenUnderThree();
        Map<Hotel, List<Room>> roomsByHotel = availableRooms.stream()
                .collect(Collectors.groupingBy(Room::getHotel));
        for (Map.Entry<Hotel, List<Room>> entry : roomsByHotel.entrySet()) {
            Hotel hotel = entry.getKey();
            List<Room> rooms = entry.getValue();
            Pair<List<Room>, Float> pair = getRoomConfigurationForAmountOfPeople(rooms, numberOfGuests);
            if (pair.getSecond() != 0) {
                availableHotels.add(hotel);
                pricesPerAdult.add(pair.getSecond());
            }
        }
        return GetHotelsBySearchQueryResponseDto.builder()
                .hotels(HotelMapper.mapList(availableHotels, pricesPerAdult))
                .build();
    }

    public boolean CheckHotelAvailability(CheckHotelAvailabilityQueryRequestDto requestDto) {
        // Step 1: Extract information from the request DTO
        LocalDateTime dateFrom = requestDto.getDateFrom();
        LocalDateTime dateTo = requestDto.getDateTo();

        UUID hotelId = requestDto.getHotelId();
        List<UUID> roomIds = requestDto.getRoomReservationsIds();

        // Step 2: Retrieve the hotel from the repository
        Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
        if (hotelOpt.isEmpty()) {
            return false;
        }

        Hotel hotel = hotelOpt.get();

        // Step 3: Filter rooms by room IDs
        List<Room> specificRooms = hotel.getRooms().stream()
                .filter(room -> roomIds.contains(room.getId()))
                .toList();


        // Step 4: check availability of all rooms
        for (Room specificRoom :specificRooms) {
            if (!isRoomAvailable(specificRoom, dateFrom, dateTo)) { return false;}
        }

        return true;
    }

    private boolean isRoomAvailable(Room room, LocalDateTime dateFrom, LocalDateTime dateTo) {
        for (RoomReservation reservation : room.getRoomReservations()) {
            if (reservation.getDateFrom().isBefore(dateTo) && reservation.getDateTo().isAfter(dateFrom)) {
                return false;
            }
        }
        return true;
    }

    public Room getRoomById(UUID uuid) {
        return roomRepository.findById(uuid).orElseThrow(RuntimeException::new);
    }

    public boolean doesRoomHaveAnyReservationsInFuture(Room room) {
        LocalDateTime date = LocalDateTime.now();
        for(RoomReservation reservation : room.getRoomReservations()) {
            if(reservation.getDateTo().isAfter(date)) return true;
        }
        return false;
    }

    public Hotel getHotel(UUID id) throws HotelNoFoundException {
        return hotelRepository.findById(id).orElseThrow(HotelNoFoundException::new);
    }

    public void createRoomFromHotel(UUID hotelId, UUID roomId, String name, int guestCapacity, float pricePerAdult,
                                    String description) {
        // hotel event projector
        RoomCreatedEvent roomCreatedEvent = RoomCreatedEvent.builder()
                .idHotel(hotelId)
                .roomId(roomId)
                .name(name)
                .guestCapacity(guestCapacity)
                .pricePerAdult(pricePerAdult)
                .description(description)
                .build();

        hotelEventProjector.project(List.of(roomCreatedEvent));
    }

    public void updateRoomFromHotel(UUID hotelId, UUID roomId, String name, int guestCapacity, float pricePerAdult,
                                    String description) {
        RoomUpdateEvent roomUpdateEvent = new RoomUpdateEvent(hotelId, roomId, name, guestCapacity, pricePerAdult, description);
        hotelEventProjector.project(List.of(roomUpdateEvent));
    }
}

