package org.microarchitecturovisco.reservationservice.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandleReservationRequestDto implements Serializable {
    private UUID id;

    @Size(min = 16, max = 16)
    private String cardNumber;

    private UUID idReservation;
}