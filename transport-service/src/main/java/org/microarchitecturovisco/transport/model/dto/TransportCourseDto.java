package org.microarchitecturovisco.transport.model.dto;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.transport.model.domain.TransportType;

@Data
@Builder
public class TransportCourseDto {
    private TransportType type;
    private LocationDto departureFromLocation;
    private LocationDto arrivalAtLocation;
}
