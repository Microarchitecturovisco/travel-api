package org.microarchitecturovisco.transport.services;

import org.junit.jupiter.api.Test;
import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.microarchitecturovisco.transport.model.domain.TransportType;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TransportsServiceTest {

    @InjectMocks
    private TransportsService transportsService;

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
        AvailableTransportsDto result = transportsService.getAvailableTransports();

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
        AvailableTransportsDto result = transportsService.getAvailableTransports();

        // Assert
        assertEquals(0, result.getDepartures().getPlane().size());
        assertEquals(0, result.getDepartures().getBus().size());
    }

    @Test
    public void testGetTransportsBySearchQuery_UUID_correct() {
        // Arrange
        GetTransportsBySearchQueryRequestDto requestDto = GetTransportsBySearchQueryRequestDto.builder()
                .uuid(java.util.UUID.randomUUID().toString())
                .dateFrom(LocalDateTime.of(2024, Month.MAY, 1, 12, 0, 0))
                .dateTo(LocalDateTime.of(2024, Month.MAY, 14, 12, 0, 0))
                .departureLocationIdsByPlane(List.of(1))
                .departureLocationIdsByBus(List.of())
                .arrivalLocationIds(List.of(6))
                .adults(2)
                .childrenUnderThree(1)
                .childrenUnderTen(1)
                .childrenUnderEighteen(1)
                .build();

        requestDto.setDepartureLocationIdsByBus(Collections.singletonList(1));
        requestDto.setDepartureLocationIdsByPlane(Collections.singletonList(2));

        when(transportRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        GetTransportsBySearchQueryResponseDto responseDto = transportsService.getTransportsBySearchQuery(requestDto);

        // Assert
        assertEquals(requestDto.getUuid(), responseDto.getUuid());
    }
}
