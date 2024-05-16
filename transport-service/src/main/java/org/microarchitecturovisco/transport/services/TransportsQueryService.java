package org.microarchitecturovisco.transport.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.domain.*;
import org.microarchitecturovisco.transport.model.dto.TransportDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBetweenLocationsRequestDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBetweenMultipleLocationsRequestDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDepartures;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBetweenLocationsResponseDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.microarchitecturovisco.transport.model.mappers.LocationMapper;
import org.microarchitecturovisco.transport.model.mappers.TransportMapper;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.microarchitecturovisco.transport.repositories.TransportEventStore;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TransportsQueryService {

    private final TransportCourseRepository transportCourseRepository;
    private final TransportRepository transportRepository;

    public List<TransportDto> getAllTransports() {
        List<Transport> transports = transportRepository.findAll();
        return TransportMapper.mapList(transports);
    }

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

    public GetTransportsBetweenLocationsResponseDto getTransportsBetweenLocations(GetTransportsBetweenLocationsRequestDto requestDto) {
        LocalDateTime dateFrom = requestDto.getDateFrom()
                .minusHours(requestDto.getDateFrom().getHour())
                .minusMinutes(requestDto.getDateFrom().getMinute());
        LocalDateTime dateTo = requestDto.getDateTo()
                .minusHours(requestDto.getDateTo().getHour())
                .minusMinutes(requestDto.getDateTo().getMinute());

        GetTransportsBySearchQueryResponseDto departureDayTransportsResponse = getTransportsBySearchQuery(GetTransportsBySearchQueryRequestDto.builder()
                .uuid(requestDto.getUuid().toString())
                .dateFrom(dateFrom)
                .dateTo(dateFrom.plusHours(23).plusMinutes(59))
                .adults(requestDto.getAdults())
                .childrenUnderEighteen(requestDto.getChildrenUnderEighteen())
                .childrenUnderTen(requestDto.getChildrenUnderTen())
                .childrenUnderThree(requestDto.getChildrenUnderThree())
                .departureLocationIdsByPlane(requestDto.getTransportType() == TransportType.PLANE ? List.of(requestDto.getDepartureLocationId()) : List.of())
                .departureLocationIdsByBus(requestDto.getTransportType() == TransportType.BUS ? List.of(requestDto.getDepartureLocationId()) : List.of())
                .arrivalLocationIds(List.of(requestDto.getArrivalLocationId()))
                .build()
        );

        GetTransportsBySearchQueryResponseDto arrivalDayTransportsResponse = getTransportsBySearchQuery(GetTransportsBySearchQueryRequestDto.builder()
                .uuid(requestDto.getUuid().toString())
                .dateFrom(dateTo)
                .dateTo(dateTo.plusHours(23).plusMinutes(59))
                .adults(requestDto.getAdults())
                .childrenUnderEighteen(requestDto.getChildrenUnderEighteen())
                .childrenUnderTen(requestDto.getChildrenUnderTen())
                .childrenUnderThree(requestDto.getChildrenUnderThree())
                .departureLocationIdsByPlane(requestDto.getTransportType() == TransportType.PLANE ? List.of(requestDto.getArrivalLocationId()) : List.of())
                .departureLocationIdsByBus(requestDto.getTransportType() == TransportType.BUS ? List.of(requestDto.getArrivalLocationId()) : List.of())
                .arrivalLocationIds(List.of(requestDto.getDepartureLocationId()))
                .build()
        );

        List<Pair<TransportDto, TransportDto>> transportPairs = new ArrayList<>();

        for (TransportDto departureDto : departureDayTransportsResponse.getTransportDtoList()) {
            for (TransportDto arrivalDto : arrivalDayTransportsResponse.getTransportDtoList()) {
                if (departureDto.getTransportCourse().getDepartureFromLocation().equals(arrivalDto.getTransportCourse().getArrivalAtLocation())  &&
                        departureDto.getTransportCourse().getArrivalAtLocation().equals(arrivalDto.getTransportCourse().getDepartureFromLocation()) &&
                        departureDto.getTransportCourse().getType().equals(arrivalDto.getTransportCourse().getType())
                ) {
                    transportPairs.add(Pair.of(departureDto, arrivalDto));
                    break;
                }
            }
        }

        return GetTransportsBetweenLocationsResponseDto.builder()
                .uuid(requestDto.getUuid())
                .transportPairs(transportPairs)
                .build();
    }

    public GetTransportsBetweenLocationsResponseDto getTransportsBetweenMultipleLocations(GetTransportsBetweenMultipleLocationsRequestDto requestDto) {
        LocalDateTime dateFrom = requestDto.getDateFrom()
                .minusHours(requestDto.getDateFrom().getHour())
                .minusMinutes(requestDto.getDateFrom().getMinute());
        LocalDateTime dateTo = requestDto.getDateTo()
                .minusHours(requestDto.getDateTo().getHour())
                .minusMinutes(requestDto.getDateTo().getMinute());

        GetTransportsBySearchQueryResponseDto departureDayTransportsResponse = getTransportsBySearchQuery(GetTransportsBySearchQueryRequestDto.builder()
                .uuid(requestDto.getUuid().toString())
                .dateFrom(dateFrom)
                .dateTo(dateFrom.plusHours(23).plusMinutes(59))
                .adults(requestDto.getAdults())
                .childrenUnderEighteen(requestDto.getChildrenUnderEighteen())
                .childrenUnderTen(requestDto.getChildrenUnderTen())
                .childrenUnderThree(requestDto.getChildrenUnderThree())
                .departureLocationIdsByPlane(requestDto.getDepartureLocationIds())
                .departureLocationIdsByBus(List.of())
                .arrivalLocationIds(requestDto.getArrivalLocationIds())
                .build()
        );

        GetTransportsBySearchQueryResponseDto arrivalDayTransportsResponse = getTransportsBySearchQuery(GetTransportsBySearchQueryRequestDto.builder()
                .uuid(requestDto.getUuid().toString())
                .dateFrom(dateTo)
                .dateTo(dateTo.plusHours(23).plusMinutes(59))
                .adults(requestDto.getAdults())
                .childrenUnderEighteen(requestDto.getChildrenUnderEighteen())
                .childrenUnderTen(requestDto.getChildrenUnderTen())
                .childrenUnderThree(requestDto.getChildrenUnderThree())
                .departureLocationIdsByPlane(requestDto.getArrivalLocationIds())
                .departureLocationIdsByBus(List.of())
                .arrivalLocationIds(requestDto.getDepartureLocationIds())
                .build()
        );

        List<Pair<TransportDto, TransportDto>> transportPairs = new ArrayList<>();

        for (TransportDto departureDto : departureDayTransportsResponse.getTransportDtoList()) {
            for (TransportDto arrivalDto : arrivalDayTransportsResponse.getTransportDtoList()) {
                if (departureDto.getTransportCourse().getDepartureFromLocation().equals(arrivalDto.getTransportCourse().getArrivalAtLocation())  &&
                        departureDto.getTransportCourse().getArrivalAtLocation().equals(arrivalDto.getTransportCourse().getDepartureFromLocation()) &&
                        departureDto.getTransportCourse().getType().equals(arrivalDto.getTransportCourse().getType())
                ) {
                    transportPairs.add(Pair.of(departureDto, arrivalDto));
                    break;
                }
            }
        }

        return GetTransportsBetweenLocationsResponseDto.builder()
                .uuid(requestDto.getUuid())
                .transportPairs(transportPairs)
                .build();
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
