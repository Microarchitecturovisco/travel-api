package org.microarchitecturovisco.offerprovider.domain.dto;

import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDto {
    private Integer idLocation;
    private String country;
    private String region;
}
