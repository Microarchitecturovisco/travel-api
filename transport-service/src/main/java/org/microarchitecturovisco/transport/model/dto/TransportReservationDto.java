package org.microarchitecturovisco.transport.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@Builder
@SuperBuilder
public class TransportReservationDto {
    private UUID idTransportReservation;
    private UUID idTransport;
    private int numberOfSeats;
}
