package org.microarchitecturovisco.offerprovider.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto implements Serializable {
    private UUID idLocation;
    private String country;
    private String region;
}
