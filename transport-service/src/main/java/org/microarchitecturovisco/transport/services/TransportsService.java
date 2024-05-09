package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.List;
import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.microarchitecturovisco.transport.model.domain.TransportType;
import org.microarchitecturovisco.transport.model.dto.transports.response.AvailableTransportsDepartures;
import org.microarchitecturovisco.transport.model.dto.transports.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.model.mappers.LocationMapper;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TransportsService {

    private final TransportCourseRepository transportCourseRepository;

    public AvailableTransportsDto getAvailableTransports() {

        List<TransportCourse> transportCourses = transportCourseRepository.findAll();

        List<Location> departuresPlane = new ArrayList<>();
        List<Location> departuresBus = new ArrayList<>();
        List<Location> arrivals = new ArrayList<>();

        for (TransportCourse transportCourse : transportCourses) {
            if (transportCourse.getDepartureFrom().getCountry().equals("Polska")) {
                if (transportCourse.getType().equals(TransportType.PLANE) && !departuresPlane.contains(transportCourse.getDepartureFrom())) {
                    departuresPlane.add(transportCourse.getDepartureFrom());
                }
                if (transportCourse.getType().equals(TransportType.BUS) && !departuresBus.contains(transportCourse.getDepartureFrom())) {
                    departuresBus.add(transportCourse.getDepartureFrom());
                }
                if (!arrivals.contains(transportCourse.getArrivalAt())) {
                    arrivals.add(transportCourse.getArrivalAt());
                }
            }
        }

        return buildAvailableTransports(departuresPlane, departuresBus, arrivals);
    }

    public AvailableTransportsDto buildAvailableTransports(
            List<Location> departuresPlane,
            List<Location> departuresBus,
            List<Location> arrivals
    ) {
        return AvailableTransportsDto.builder()
                .arrivals(LocationMapper.mapList(arrivals))
                .departures(AvailableTransportsDepartures.builder()
                        .plane(LocationMapper.mapList(departuresPlane))
                        .bus(LocationMapper.mapList(departuresBus))
                        .build())
                .build();
    }
}
