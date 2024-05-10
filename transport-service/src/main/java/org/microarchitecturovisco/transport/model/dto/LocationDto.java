package org.microarchitecturovisco.transport.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class LocationDto implements Serializable {
    private Integer idLocation;
    private String country;
    private String region;
}
