package org.microarchitecturovisco.reservationservice.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
    @Size(min = 16, max = 16)
    private String cardNumber;

    private String idReservation;
}
