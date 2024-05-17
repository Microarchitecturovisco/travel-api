package org.microarchitecturovisco.offerprovider.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportDto implements Serializable {
    private UUID idTransport;

    private LocalDateTime departureDate;
    private Integer capacity;
    private Float pricePerAdult;

    private TransportCourseDto transportCourse;
}
