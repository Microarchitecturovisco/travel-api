package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.model.dto.transports.LocationDto;
import org.microarchitecturovisco.transport.model.dto.transports.response.AvailableTransportsDepartures;
import org.microarchitecturovisco.transport.model.dto.transports.response.AvailableTransportsDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransportsService {

    public AvailableTransportsDto getAvailableTransports() {
        return buildAvailableTransports(List.of(), List.of(), List.of());
    }

    public AvailableTransportsDto buildAvailableTransports(
            List<Location> departuresPlane,
            List<Location> departuresBus,
            List<Location> arrivals
    ) {
        List<LocationDto> locations = List.of(
                LocationDto.builder().idLocation(1).country("Poland").region("Gdansk").build()
        );

        return AvailableTransportsDto.builder()
                .arrivals(locations)
                .departures(AvailableTransportsDepartures.builder()
                        .plane(locations)
                        .bus(locations)
                        .build())
                .build();
    }
}
