package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.microarchitecturovisco.transport.model.domain.TransportType;
import org.microarchitecturovisco.transport.model.dto.TransportCourseDto;
import org.microarchitecturovisco.transport.model.dto.TransportDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDepartures;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.microarchitecturovisco.transport.model.mappers.LocationMapper;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransportsService {

    private final TransportCourseRepository transportCourseRepository;
    private final TransportRepository transportRepository;

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

    public GetTransportsBySearchQueryResponseDto getTransportsBySearchQuery(GetTransportsBySearchQueryRequestDto requestDto) {

        List<Transport> transports = transportRepository.findAll();

        Transport testTransport = transports.getFirst();
        TransportCourseDto transportCourseDto = TransportCourseDto.builder()
                .type(testTransport.getCourse().getType())
                .departureFromLocation(LocationMapper.map(testTransport.getCourse().getDepartureFrom()))
                .arrivalAtLocation(LocationMapper.map(testTransport.getCourse().getArrivalAt()))
                .build();

        return GetTransportsBySearchQueryResponseDto.builder()
                .transportDtoList(
                        List.of(TransportDto.builder()
                                .idTransport(testTransport.getId())
                                .transportCourse(transportCourseDto)
                                .departureDate(testTransport.getDepartureDate())
                                .capacity(testTransport.getCapacity())
                                .pricePerAdult(testTransport.getPricePerAdult())
                                .build())

                ).build();
    }
}
