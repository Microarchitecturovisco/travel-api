package org.microarchitecturovisco.offerprovider.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto implements Serializable {
    private Integer idLocation;
    private String country;
    private String region;
}
