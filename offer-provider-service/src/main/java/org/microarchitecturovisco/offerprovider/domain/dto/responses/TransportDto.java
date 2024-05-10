package org.microarchitecturovisco.offerprovider.domain.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransportDto {
    private Integer idTransport;

    private LocalDateTime departureDate;
    private Integer capacity;
    private Float pricePerAdult;

    private TransportCourseDto transportCourse;
}
