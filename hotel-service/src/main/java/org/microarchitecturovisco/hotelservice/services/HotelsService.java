package org.microarchitecturovisco.hotelservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.model.domain.*;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelsBySearchQueryRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelsBySearchQueryResponseDto;
import org.microarchitecturovisco.hotelservice.model.mappers.HotelMapper;
import org.microarchitecturovisco.hotelservice.repositories.HotelRepository;
import org.microarchitecturovisco.hotelservice.repositories.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelsService {

    private final RoomRepository roomRepository;


    public GetHotelsBySearchQueryResponseDto GetHotelsBySearchQuery(GetHotelsBySearchQueryRequestDto requestDto) {
        LocalDateTime dateFrom = requestDto.getDateFrom();
        LocalDateTime dateTo = requestDto.getDateTo();

        List<Hotel> availableHotels = new ArrayList<>();
        List<Float> pricesPerAdult = new ArrayList<>();

        List<Integer> arrivalLocationIds = requestDto.getArrivalLocationIds();

        List<Room> availableRooms = roomRepository.findAvailableRoomsByLocationAndDate(arrivalLocationIds, dateFrom, dateTo);
        int numberOfGuests = requestDto.getAdults() + requestDto.getChildrenUnderEighteen() + requestDto.getChildrenUnderTen() + requestDto.getChildrenUnderThree();
        Map<Hotel, List<Room>> roomsByHotel = availableRooms.stream()
                .collect(Collectors.groupingBy(Room::getHotel));
        for (Map.Entry<Hotel, List<Room>> entry : roomsByHotel.entrySet()) {
            int tempGuests = numberOfGuests;
            Hotel hotel = entry.getKey();
            List<Room> rooms = entry.getValue();
            List<Room> sortedRooms = new ArrayList<>(rooms.stream()
                    .sorted(Comparator.comparingInt(Room::getGuestCapacity))
                    .toList());
            float currentPrice = 0;
            int currentPeople = 0;
            while (tempGuests > 0) {
                for (int i = 0; i < sortedRooms.size(); i++) {
                    if (sortedRooms.get(i).getGuestCapacity() >= tempGuests || i == sortedRooms.size() - 1) {
                        tempGuests-= sortedRooms.get(i).getGuestCapacity();
                        currentPrice = ((currentPrice * currentPeople) + (sortedRooms.get(i).getPricePerAdult() * sortedRooms.get(i).getGuestCapacity()))
                                / (currentPeople + sortedRooms.get(i).getGuestCapacity());
                        currentPeople += sortedRooms.get(i).getGuestCapacity();
                        sortedRooms.remove(i);

                        break;
                    }
                }
                if (sortedRooms.isEmpty()) {break;}
            }
            if (tempGuests <= 0) {
                availableHotels.add(hotel);
                pricesPerAdult.add(currentPrice);
            }
        }
        return GetHotelsBySearchQueryResponseDto.builder()
                .uuid(requestDto.getUuid())
                .pricePerAdult(pricesPerAdult)
                .hotels(HotelMapper.mapList(availableHotels))
                .build();
    }

}

