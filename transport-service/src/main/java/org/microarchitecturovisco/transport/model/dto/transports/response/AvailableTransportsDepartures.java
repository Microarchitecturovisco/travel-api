package org.microarchitecturovisco.transport.model.dto.transports.response;


import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.transport.model.dto.transports.LocationDto;

import java.util.List;

@Data
@Builder
public class AvailableTransportsDepartures {
    private List<LocationDto> bus;
    private List<LocationDto> plane;
}
