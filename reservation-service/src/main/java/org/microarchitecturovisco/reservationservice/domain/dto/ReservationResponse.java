package org.microarchitecturovisco.reservationservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@ToString
public class ReservationResponse {
    private ReservationRequest reservationRequest;
    private String message;
}
