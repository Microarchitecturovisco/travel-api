package org.microarchitecturovisco.transport.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TransportReservationDto {
    private UUID reservationId;
    private UUID idTransport;
    private int numberOfSeats;
}
