package org.microarchitecturovisco.transport.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.microarchitecturovisco.transport.model.domain.Location;
import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.microarchitecturovisco.transport.model.domain.TransportType;
import org.microarchitecturovisco.transport.model.dto.transports.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.repositories.TransportCourseRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    public void testGetAvailableTransports() {
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
    }
}