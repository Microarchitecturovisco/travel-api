package org.microarchitecturovisco.reservationservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class HandleReservationResponseDto implements Serializable {
    private UUID id;

    private boolean transactionApproved;
}
