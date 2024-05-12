package org.microarchitecturovisco.hotelservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.model.domain.*;
import org.microarchitecturovisco.hotelservice.model.dto.TransportCourseDto;
import org.microarchitecturovisco.hotelservice.model.dto.TransportDto;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelsBySearchQueryRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.AvailableTransportsDepartures;
import org.microarchitecturovisco.hotelservice.model.dto.response.AvailableTransportsDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.microarchitecturovisco.hotelservice.model.mappers.LocationMapper;
import org.microarchitecturovisco.hotelservice.model.mappers.TransportMapper;
import org.microarchitecturovisco.hotelservice.repositories.HotelRepository;
import org.microarchitecturovisco.hotelservice.repositories.RoomRepository;
import org.microarchitecturovisco.hotelservice.repositories.TransportCourseRepository;
import org.microarchitecturovisco.hotelservice.repositories.TransportRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelsService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;


    public GetHotelsBySearchQueryResponseDto GetHotelsBySearchQuery(GetHotelsBySearchQueryRequestDto requestDto) {

        List<Hotel> hotels = hotelRepository.findAll();

        List<Hotel> availableHotels = new ArrayList<>();

        List<Integer> arrivalLocationIds = requestDto.getArrivalLocationIds();

        List<Hotel> hotelsInChosenLocation = hotelRepository.findHotelsByArrivalLocationIds(arrivalLocationIds);
        Integer numberOfGuests = requestDto.getAdults() + requestDto.getChildrenUnderEighteen() + requestDto.getChildrenUnderTen() + requestDto.getChildrenUnderThree();
        for (Hotel hotel : hotelsInChosenLocation) {
            List<Room> hotelRooms = hotel.getRooms();

        }

    }

}

