package org.microarchitecturovisco.transport.model.dto;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.transport.model.domain.TransportType;

import java.io.Serializable;

@Data
@Builder
public class TransportCourseDto implements Serializable {
    private TransportType type;
    private LocationDto departureFromLocation;
    private LocationDto arrivalAtLocation;
}
