package org.microarchitecturovisco.transport.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class TransportDto implements Serializable {
    private Integer idTransport;

    private LocalDateTime departureDate;
    private Integer capacity;
    private Float pricePerAdult;

    private TransportCourseDto transportCourse;
}
