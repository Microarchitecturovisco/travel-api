package org.microarchitecturovisco.reservationservice.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransportReservationResponse {
    private String type;
    private LocationReservationResponse departureFrom;
    private LocationReservationResponse departureTo;
}
