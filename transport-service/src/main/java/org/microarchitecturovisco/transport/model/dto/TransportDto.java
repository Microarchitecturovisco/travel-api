package org.microarchitecturovisco.transport.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransportDto implements Serializable {
    private UUID idTransport;

    private LocalDateTime departureDate;
    private Integer capacity;
    private Float pricePerAdult;

    private TransportCourseDto transportCourse;
}
