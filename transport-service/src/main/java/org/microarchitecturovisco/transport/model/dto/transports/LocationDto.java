package org.microarchitecturovisco.transport.model.dto.transports;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDto {
    private Integer idLocation;
    private String country;
    private String region;
}
