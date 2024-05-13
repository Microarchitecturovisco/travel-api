package org.microarchitecturovisco.offerprovider.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportDto implements Serializable {
    private Integer idTransport;

    private LocalDateTime departureDate;
    private Integer capacity;
    private Float pricePerAdult;

    private TransportCourseDto transportCourse;
}
