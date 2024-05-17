package org.microarchitecturovisco.offerprovider.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.LocationDto;
import org.microarchitecturovisco.offerprovider.domain.dto.TransportType;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportCourseDto implements Serializable {
    private UUID idTransportCourse;
    private TransportType type;
    private LocationDto departureFromLocation;
    private LocationDto arrivalAtLocation;
}
