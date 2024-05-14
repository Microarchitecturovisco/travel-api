package org.microarchitecturovisco.transport.services;

import org.junit.jupiter.api.Test;
import org.microarchitecturovisco.transport.model.domain.*;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TransportsQueryServiceTest {

    @InjectMocks
    private TransportsQueryService transportsQueryService;

    @Mock
    private TransportCourseRepository transportCourseRepository;

    @Mock
    private TransportRepository transportRepository;

    @Test
    public void testGetAvailableTransports_returnsCorrectDto() {
        // Arrange
        TransportCourse transportCourse = new TransportCourse();
        Location location = new Location("Polska", "Warsaw");
        transportCourse.setDepartureFrom(location);
        transportCourse.setArrivalAt(location);
        transportCourse.setType(TransportType.PLANE);
        when(transportCourseRepository.findAll()).thenReturn(List.of(transportCourse));

        // Act
        AvailableTransportsDto result = transportsQueryService.getAvailableTransports();

        // Assert
        assertEquals(1, result.getDepartures().getPlane().size());
        assertEquals("Warsaw", result.getDepartures().getPlane().getFirst().getRegion());
        assertEquals("Polska", result.getDepartures().getPlane().getFirst().getCountry());
        assertEquals(0, result.getDepartures().getBus().size());
        assertEquals(1, result.getArrivals().size());
    }

    @Test
    public void testGetAvailableTransports_returnsEmptyList () {
        // Arrange
        when(transportCourseRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        AvailableTransportsDto result = transportsQueryService.getAvailableTransports();

        // Assert
        assertEquals(0, result.getDepartures().getPlane().size());
        assertEquals(0, result.getDepartures().getBus().size());
    }

    @Test
    public void testGetTransportsBySearchQuery_UUID_correct() {

        UUID locationIdA = UUID.randomUUID();
        UUID locationIdB = UUID.randomUUID();

        // Arrange
        GetTransportsBySearchQueryRequestDto requestDto = GetTransportsBySearchQueryRequestDto.builder()
                .uuid(java.util.UUID.randomUUID().toString())
                .dateFrom(LocalDateTime.of(2024, Month.MAY, 1, 12, 0, 0))
                .dateTo(LocalDateTime.of(2024, Month.MAY, 14, 12, 0, 0))
                .departureLocationIdsByPlane(List.of(locationIdA))
                .departureLocationIdsByBus(List.of())
                .arrivalLocationIds(List.of(locationIdB))
                .adults(2)
                .childrenUnderThree(1)
                .childrenUnderTen(1)
                .childrenUnderEighteen(1)
                .build();

        requestDto.setDepartureLocationIdsByBus(Collections.singletonList(locationIdA));
        requestDto.setDepartureLocationIdsByPlane(Collections.singletonList(locationIdB));

        when(transportRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        GetTransportsBySearchQueryResponseDto responseDto = transportsQueryService.getTransportsBySearchQuery(requestDto);

        // Assert
        assertEquals(requestDto.getUuid(), responseDto.getUuid());
    }

    @Test
    public void testGetTransportsBySearchQuery_DateRange() {
        // Arrange
        GetTransportsBySearchQueryRequestDto requestDto = GetTransportsBySearchQueryRequestDto.builder()
                .uuid(java.util.UUID.randomUUID().toString())
                .dateFrom(LocalDateTime.of(2024, Month.MAY, 6, 12, 0, 0))
                .dateTo(LocalDateTime.of(2024, Month.MAY, 12, 12, 0, 0))
                .build();

        UUID idA = UUID.randomUUID();
        UUID idB = UUID.randomUUID();
        UUID idC = UUID.randomUUID();

        UUID locationIdA = UUID.randomUUID();
        UUID locationIdB = UUID.randomUUID();

        UUID reservationIdA = UUID.randomUUID();
        UUID reservationIdB = UUID.randomUUID();
        UUID reservationIdC = UUID.randomUUID();

        Transport transportA = Transport.builder()
                .id(idA)
                .departureDate(LocalDateTime.of(2024, Month.MAY, 5, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdA).country("Poland").region("Gdańsk").build())
                        .arrivalAt(Location.builder().id(locationIdB).country("Tunezja").region("Tunis").build())
                        .build())
                .capacity(100)
                .pricePerAdult(200.0f)
                .transportReservations(List.of(
                        TransportReservation.builder().id(reservationIdA).numberOfSeats(5).build(),
                        TransportReservation.builder().id(reservationIdB).numberOfSeats(2).build(),
                        TransportReservation.builder().id(reservationIdC).numberOfSeats(3).build()))
                .build();

        Transport transportB = Transport.builder()
                .id(idB)
                .departureDate(LocalDateTime.of(2024, Month.MAY, 10, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdA).country("Poland").region("Gdańsk").build())
                        .arrivalAt(Location.builder().id(locationIdB).country("Tunezja").region("Tunis").build())
                        .build())
                .capacity(100)
                .pricePerAdult(200.0f)
                .transportReservations(List.of(
                        TransportReservation.builder().id(reservationIdA).numberOfSeats(5).build(),
                        TransportReservation.builder().id(reservationIdB).numberOfSeats(2).build(),
                        TransportReservation.builder().id(reservationIdC).numberOfSeats(3).build()))
                .build();

        Transport transportC = Transport.builder()
                .id(idC)
                .departureDate(LocalDateTime.of(2024, Month.MAY, 15, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdA).country("Poland").region("Gdańsk").build())
                        .arrivalAt(Location.builder().id(locationIdB).country("Tunezja").region("Tunis").build())
                        .build())
                .capacity(100)
                .pricePerAdult(200.0f)
                .transportReservations(List.of(
                        TransportReservation.builder().id(reservationIdA).numberOfSeats(5).build(),
                        TransportReservation.builder().id(reservationIdB).numberOfSeats(2).build(),
                        TransportReservation.builder().id(reservationIdC).numberOfSeats(3).build()))
                .build();

        when(transportRepository.findAll()).thenReturn(List.of(transportA, transportB, transportC));

        // Act
        GetTransportsBySearchQueryResponseDto responseDto = transportsQueryService.getTransportsBySearchQuery(requestDto);

        // Assert
        assertEquals(idB, responseDto.getTransportDtoList().getFirst().getIdTransport());
        assertEquals(1, responseDto.getTransportDtoList().size());

    }

    @Test
    public void testGetTransportsBySearchQuery_LocationIds() {
        // Arrange
        UUID idA = UUID.randomUUID();
        UUID idB = UUID.randomUUID();

        UUID locationIdA = UUID.randomUUID();
        UUID locationIdB = UUID.randomUUID();
        UUID locationIdC = UUID.randomUUID();

        UUID reservationIdA = UUID.randomUUID();
        UUID reservationIdB = UUID.randomUUID();
        UUID reservationIdC = UUID.randomUUID();

        GetTransportsBySearchQueryRequestDto requestDto = GetTransportsBySearchQueryRequestDto.builder()
                .uuid(java.util.UUID.randomUUID().toString())
                .departureLocationIdsByPlane(List.of(locationIdA))
                .departureLocationIdsByBus(List.of())
                .arrivalLocationIds(List.of(locationIdB))
                .build();

        Transport transportA = Transport.builder()
                .id(idA)
                .departureDate(LocalDateTime.of(2024, Month.MAY, 5, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdA).country("Poland").region("Gdańsk").build())
                        .arrivalAt(Location.builder().id(locationIdB).country("Tunezja").region("Tunis").build())
                        .build())
                .capacity(100)
                .pricePerAdult(200.0f)
                .transportReservations(List.of(
                        TransportReservation.builder().id(reservationIdA).numberOfSeats(5).build(),
                        TransportReservation.builder().id(reservationIdB).numberOfSeats(2).build(),
                        TransportReservation.builder().id(reservationIdC).numberOfSeats(3).build()))
                .build();

        Transport transportB = Transport.builder()
                .id(idB)
                .departureDate(LocalDateTime.of(2024, Month.MAY, 10, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdC).country("Poland").region("Wrocław").build())
                        .arrivalAt(Location.builder().id(locationIdB).country("Tunezja").region("Tunis").build())
                        .build())
                .capacity(100)
                .pricePerAdult(200.0f)
                .transportReservations(List.of(
                        TransportReservation.builder().id(reservationIdA).numberOfSeats(5).build(),
                        TransportReservation.builder().id(reservationIdB).numberOfSeats(2).build(),
                        TransportReservation.builder().id(reservationIdC).numberOfSeats(3).build()))
                .build();

        when(transportRepository.findAll()).thenReturn(List.of(transportA, transportB));

        // Act
        GetTransportsBySearchQueryResponseDto responseDto = transportsQueryService.getTransportsBySearchQuery(requestDto);

        // Assert
        assertEquals(idA, responseDto.getTransportDtoList().getFirst().getIdTransport());
    }

    @Test
    public void testGetTransportsBySearchQuery_People() {
        // Arrange
        UUID idA = UUID.randomUUID();
        UUID idB = UUID.randomUUID();
        UUID idC = UUID.randomUUID();
        UUID idD = UUID.randomUUID();

        UUID locationIdA = UUID.randomUUID();
        UUID locationIdB = UUID.randomUUID();
        UUID locationIdC = UUID.randomUUID();
        UUID locationIdD = UUID.randomUUID();

        UUID reservationIdA = UUID.randomUUID();
        UUID reservationIdB = UUID.randomUUID();
        UUID reservationIdC = UUID.randomUUID();

        GetTransportsBySearchQueryRequestDto requestDto = GetTransportsBySearchQueryRequestDto.builder()
                .uuid(java.util.UUID.randomUUID().toString())
                .adults(2)
                .childrenUnderThree(1)
                .childrenUnderTen(1)
                .childrenUnderEighteen(2)
                .build();

        // 5 seats left (just what the user needs)
        Transport transportA = Transport.builder()
                .id(idA)
                .departureDate(LocalDateTime.of(2024, Month.MAY, 5, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdA).country("Poland").region("Gdańsk").build())
                        .arrivalAt(Location.builder().id(locationIdB).country("Tunezja").region("Tunis").build())
                        .build())
                .capacity(15)
                .pricePerAdult(200.0f)
                .transportReservations(List.of(
                        TransportReservation.builder().id(reservationIdA).numberOfSeats(5).build(),
                        TransportReservation.builder().id(reservationIdB).numberOfSeats(2).build(),
                        TransportReservation.builder().id(reservationIdC).numberOfSeats(3).build()))
                .build();

        // full capacity
        Transport transportB = Transport.builder()
                .id(idB)
                .departureDate(LocalDateTime.of(2024, Month.MAY, 10, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdC).country("Poland").region("Wrocław").build())
                        .arrivalAt(Location.builder().id(locationIdB).country("Tunezja").region("Tunis").build())
                        .build())
                .capacity(10)
                .pricePerAdult(200.0f)
                .transportReservations(List.of(
                        TransportReservation.builder().id(reservationIdA).numberOfSeats(5).build(),
                        TransportReservation.builder().id(reservationIdB).numberOfSeats(2).build(),
                        TransportReservation.builder().id(reservationIdC).numberOfSeats(3).build()))
                .build();

        // 3 seats left (not enough for the user)
        Transport transportC = Transport.builder()
                .id(idC)
                .departureDate(LocalDateTime.of(2024, Month.MAY, 10, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdC).country("Poland").region("Wrocław").build())
                        .arrivalAt(Location.builder().id(locationIdB).country("Tunezja").region("Tunis").build())
                        .build())
                .capacity(13)
                .pricePerAdult(200.0f)
                .transportReservations(List.of(
                        TransportReservation.builder().id(reservationIdA).numberOfSeats(5).build(),
                        TransportReservation.builder().id(reservationIdB).numberOfSeats(2).build(),
                        TransportReservation.builder().id(reservationIdC).numberOfSeats(3).build()))
                .build();

        // 30 seats left (more than the user needs)
        Transport transportD = Transport.builder()
                .id(idD)
                .departureDate(LocalDateTime.of(2024, Month.MAY, 10, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdA).country("Poland").region("Gdańsk").build())
                        .arrivalAt(Location.builder().id(locationIdD).country("Egipt").region("Kair").build())
                        .build())
                .capacity(40)
                .pricePerAdult(200.0f)
                .transportReservations(List.of(
                        TransportReservation.builder().id(reservationIdA).numberOfSeats(5).build(),
                        TransportReservation.builder().id(reservationIdB).numberOfSeats(2).build(),
                        TransportReservation.builder().id(reservationIdC).numberOfSeats(3).build()))
                .build();

        when(transportRepository.findAll()).thenReturn(List.of(transportA, transportB, transportC, transportD));

        // Act
        GetTransportsBySearchQueryResponseDto responseDto = transportsQueryService.getTransportsBySearchQuery(requestDto);

        // Assert
        assertEquals(2, responseDto.getTransportDtoList().size());
        assertEquals(idA, responseDto.getTransportDtoList().getFirst().getIdTransport());
        assertEquals(idD, responseDto.getTransportDtoList().getLast().getIdTransport());

    }
}
