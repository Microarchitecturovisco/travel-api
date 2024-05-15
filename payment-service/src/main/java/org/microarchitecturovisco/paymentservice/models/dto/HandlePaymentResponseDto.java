package org.microarchitecturovisco.paymentservice.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class HandlePaymentResponseDto {
    private UUID id;

    private boolean transactionApproved;
}
