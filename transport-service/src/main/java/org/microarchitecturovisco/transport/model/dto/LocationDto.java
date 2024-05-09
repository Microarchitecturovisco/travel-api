package org.microarchitecturovisco.transport.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDto {
    private Integer idLocation;
    private String country;
    private String region;
}
