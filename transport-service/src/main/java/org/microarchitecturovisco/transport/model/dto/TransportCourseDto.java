package org.microarchitecturovisco.transport.model.dto;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.transport.model.domain.TransportType;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
public class TransportCourseDto implements Serializable {
    private UUID idTransportCourse;
    private TransportType type;
    private LocationDto departureFromLocation;
    private LocationDto arrivalAtLocation;
}
