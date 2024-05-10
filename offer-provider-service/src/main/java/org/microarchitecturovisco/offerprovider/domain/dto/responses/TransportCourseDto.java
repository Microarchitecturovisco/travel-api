package org.microarchitecturovisco.offerprovider.domain.dto.responses;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.offerprovider.domain.dto.LocationDto;
import org.microarchitecturovisco.offerprovider.domain.dto.TransportType;

@Data
@Builder
public class TransportCourseDto {
    private TransportType type;
    private LocationDto departureFromLocation;
    private LocationDto arrivalAtLocation;
}
