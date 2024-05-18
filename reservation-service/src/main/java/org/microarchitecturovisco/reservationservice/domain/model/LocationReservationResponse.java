package org.microarchitecturovisco.reservationservice.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationReservationResponse {
    private String country;
    private String region;
}
