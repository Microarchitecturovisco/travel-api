package org.microarchitecturovisco.hotelservice.model.mappers;

import org.microarchitecturovisco.hotelservice.model.domain.Location;
import org.microarchitecturovisco.hotelservice.model.dto.LocationDto;

public class LocationMapper {
    public static LocationDto map(Location location) {
        return LocationDto.builder()
                .idLocation(location.getId())
                .country(location.getCountry())
                .region(location.getRegion())
                .build();
    }
}
