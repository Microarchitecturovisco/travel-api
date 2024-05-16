package org.microarchitecturovisco.hotelservice.model.mappers;

import org.microarchitecturovisco.hotelservice.model.domain.Location;
import org.microarchitecturovisco.hotelservice.model.dto.LocationDto;

import java.util.List;

public class LocationMapper {
    public static LocationDto map(Location location) {
        return LocationDto.builder()
                .idLocation(location.getId())
                .country(location.getCountry())
                .region(location.getRegion())
                .build();
    }

    public static Location map(LocationDto dto) {
        return Location.builder()
                .id(dto.getIdLocation())
                .country(dto.getCountry())
                .region(dto.getRegion())
                .build();
    }

    public static List<LocationDto> mapList(List<Location> locations) {
        return locations.stream().map(LocationMapper::map).toList();
    }
}
