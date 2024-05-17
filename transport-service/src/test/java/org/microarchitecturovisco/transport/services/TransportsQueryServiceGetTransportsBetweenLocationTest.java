package org.microarchitecturovisco.transport.services;

import org.junit.jupiter.api.Test;
import org.microarchitecturovisco.transport.model.domain.*;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBetweenLocationsRequestDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBetweenLocationsResponseDto;
import org.microarchitecturovisco.transport.repositories.TransportRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TransportsQueryServiceGetTransportsBetweenLocationTest {

    @InjectMocks
    private TransportsQueryService transportsQueryService;

    @Mock
    private TransportRepository transportRepository;

    @Test
    public void testGetTransportsBetweenLocations_successful() {
        // Arrange
        UUID idA = UUID.randomUUID();
        UUID idB = UUID.randomUUID();
        UUID idC = UUID.randomUUID();
        UUID idD = UUID.randomUUID();

        UUID locationIdA = UUID.randomUUID();
        UUID locationIdB = UUID.randomUUID();
        UUID locationIdC = UUID.randomUUID();

        UUID reservationIdA = UUID.randomUUID();
        UUID reservationIdB = UUID.randomUUID();
        UUID reservationIdC = UUID.randomUUID();

        GetTransportsBetweenLocationsRequestDto requestDto = GetTransportsBetweenLocationsRequestDto.builder()
                .uuid(java.util.UUID.randomUUID())
                .departureLocationId(locationIdA)
                .arrivalLocationId(locationIdB)
                .dateFrom(LocalDateTime.of(2024, Month.MAY, 5, 14, 0, 0))
                .dateTo(LocalDateTime.of(2024, Month.MAY, 10, 18, 0, 0))
                .transportType(TransportType.PLANE)
                .adults(2)
                .childrenUnderEighteen(2)
                .childrenUnderTen(0)
                .childrenUnderThree(0)
                .build();

        Transport transportA = Transport.builder()
                .id(idA)
                .departureDate(LocalDateTime.of(2024, Month.MAY, 5, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdA).country("Polska").region("Gdańsk").build())
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
                        .departureFrom(Location.builder().id(locationIdB).country("Tunezja").region("Tunis").build())
                        .arrivalAt(Location.builder().id(locationIdA).country("Polska").region("Gdańsk").build())
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
                .departureDate(LocalDateTime.of(2024, Month.MAY, 5, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdC).country("Polska").region("Wrocław").build())
                        .arrivalAt(Location.builder().id(locationIdB).country("Tunezja").region("Tunis").build())
                        .build())
                .capacity(100)
                .pricePerAdult(200.0f)
                .transportReservations(List.of(
                        TransportReservation.builder().id(reservationIdA).numberOfSeats(5).build(),
                        TransportReservation.builder().id(reservationIdB).numberOfSeats(2).build(),
                        TransportReservation.builder().id(reservationIdC).numberOfSeats(3).build()))
                .build();

        Transport transportD = Transport.builder()
                .id(idD)
                .departureDate(LocalDateTime.of(2024, Month.MAY, 10, 12, 0, 0))
                .course(TransportCourse.builder()
                        .type(TransportType.PLANE)
                        .departureFrom(Location.builder().id(locationIdB).country("Tunezja").region("Tunis").build())
                        .arrivalAt(Location.builder().id(locationIdC).country("Polska").region("Wrocław").build())
                        .build())
                .capacity(100)
                .pricePerAdult(200.0f)
                .transportReservations(List.of(
                        TransportReservation.builder().id(reservationIdA).numberOfSeats(5).build(),
                        TransportReservation.builder().id(reservationIdB).numberOfSeats(2).build(),
                        TransportReservation.builder().id(reservationIdC).numberOfSeats(3).build()))
                .build();

        when(transportRepository.findAll()).thenReturn(List.of(transportA, transportB, transportC, transportD));

        // Act
        GetTransportsBetweenLocationsResponseDto responseDto = transportsQueryService.getTransportsBetweenLocations(requestDto);

        // Assert
        assertEquals(responseDto.getTransportPairs().getFirst().getFirst().getIdTransport(), idA);
        assertEquals(responseDto.getTransportPairs().getFirst().getLast().getIdTransport(), idB);
    }
}
