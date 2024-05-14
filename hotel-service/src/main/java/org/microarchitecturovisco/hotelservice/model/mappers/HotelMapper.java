package org.microarchitecturovisco.hotelservice.model.mappers;

import org.microarchitecturovisco.hotelservice.model.domain.Hotel;
import org.microarchitecturovisco.hotelservice.model.dto.HotelDto;

import java.util.List;

public class HotelMapper {
    public static HotelDto map(Hotel hotel) {
        return HotelDto.builder()
                .hotelId(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .rating(hotel.getRating())
                .photos(hotel.getPhotos())
                .location(LocationMapper.map(hotel.getLocation()))
                .build();
    }

    public static List<HotelDto> mapList(List<Hotel> hotels) {
        return hotels.stream().map(HotelMapper::map).toList();
    }
}
