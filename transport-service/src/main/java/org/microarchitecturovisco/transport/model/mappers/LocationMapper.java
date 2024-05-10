package org.microarchitecturovisco.transport.model.mappers;

import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.model.dto.LocationDto;

import java.util.List;

public class LocationMapper {
    public static LocationDto map(Location location) {
        return LocationDto.builder()
                .idLocation(location.getId())
                .country(location.getCountry())
                .region(location.getRegion())
                .build();
    }

    public static List<LocationDto> mapList(List<Location> locations) {
        return locations.stream().map(LocationMapper::map).toList();
    }
}
