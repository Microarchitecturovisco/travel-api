package org.microarchitecturovisco.reservationservice.queues.hotels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@ToString
public class ReservationResponse {
    private ReservationRequest reservationRequest;
    private String message;
}
