package org.microarchitecturovisco.reservationservice.queues.hotels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@ToString
public class ReservationResponse implements Serializable {
    private String message;
}
