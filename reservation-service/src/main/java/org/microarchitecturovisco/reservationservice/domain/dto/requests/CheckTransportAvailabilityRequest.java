package org.microarchitecturovisco.reservationservice.domain.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckTransportAvailabilityRequest implements Serializable {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    private UUID transportReservationsIdFrom;
    private UUID transportReservationsIdArrival;

    private int numberOfGuests;
}


