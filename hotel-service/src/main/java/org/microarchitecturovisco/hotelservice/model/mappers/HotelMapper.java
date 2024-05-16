package org.microarchitecturovisco.hotelservice.model.mappers;

import org.microarchitecturovisco.hotelservice.model.domain.Hotel;
import org.microarchitecturovisco.hotelservice.model.dto.HotelResponseDto;

import java.util.ArrayList;
import java.util.List;

public class HotelMapper {
    public static HotelResponseDto map(Hotel hotel, float price) {
        return HotelResponseDto.builder()
                .hotelId(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .rating(hotel.getRating())
                .photos(hotel.getPhotos())
                .location(LocationMapper.map(hotel.getLocation()))
                .pricePerAdult(price)
                .build();
    }

    public static List<HotelResponseDto> mapList(List<Hotel> hotels, List<Float> prices) {
        List<HotelResponseDto> hotelDtos = new ArrayList<>();
        for (int i = 0; i < hotels.size(); i++) {
            hotelDtos.add(map(hotels.get(i), prices.get(i)));
        }
        return hotelDtos;
    }
}
