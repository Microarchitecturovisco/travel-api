package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.domain.*;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDepartures;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.microarchitecturovisco.transport.model.mappers.LocationMapper;
import org.microarchitecturovisco.transport.model.mappers.TransportMapper;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransportsQueryService {

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

        List<Transport> filteredTransports = new ArrayList<>();

        List<UUID> mergedDepartureLocationIds = new ArrayList<>();
        if (requestDto.getDepartureLocationIdsByPlane() != null) {
            mergedDepartureLocationIds.addAll(requestDto.getDepartureLocationIdsByPlane());
        }
        if (requestDto.getDepartureLocationIdsByBus() != null) {
            mergedDepartureLocationIds.addAll(requestDto.getDepartureLocationIdsByBus());
        }

        for (Transport transport : transports) {
            if ((requestDto.getDateFrom() != null || requestDto.getDateTo() != null) &&
                    (transport.getDepartureDate().isBefore(requestDto.getDateFrom()) || transport.getDepartureDate().isAfter(requestDto.getDateTo()))) {
                continue;
            }

            if ((requestDto.getAdults() != null || requestDto.getChildrenUnderTen() != null || requestDto.getChildrenUnderThree() != null || requestDto.getChildrenUnderEighteen() != null ) &&
                    !canTransportAccommodateRequestedPeople(transport, requestDto.getAdults(), requestDto.getChildrenUnderTen(), requestDto.getChildrenUnderEighteen())) {
                continue;
            }

            if (!mergedDepartureLocationIds.isEmpty() && !mergedDepartureLocationIds.contains(transport.getCourse().getDepartureFrom().getId())) {
                continue;
            }

            if (requestDto.getArrivalLocationIds() != null &&
                    !requestDto.getArrivalLocationIds().isEmpty() &&
                    !requestDto.getArrivalLocationIds().contains(transport.getCourse().getArrivalAt().getId())) {
                continue;
            }

            filteredTransports.add(transport);
        }

        return GetTransportsBySearchQueryResponseDto.builder()
                .uuid(requestDto.getUuid())
                .transportDtoList(
                        TransportMapper.mapList(filteredTransports)
                ).build();
    }

    public boolean canTransportAccommodateRequestedPeople(
            Transport transport,
            Integer adults,
            Integer childrenUnderTen,
            Integer childrenUnderEighteen) {
        return transport.getCapacity() - getTransportOccupiedSeats(transport) - adults - childrenUnderTen - childrenUnderEighteen >= 0;
    }

    public Integer getTransportOccupiedSeats(Transport transport) {
        return transport.getTransportReservations()
                .stream()
                .mapToInt(TransportReservation::getNumberOfSeats)
                .sum();
    }
}
