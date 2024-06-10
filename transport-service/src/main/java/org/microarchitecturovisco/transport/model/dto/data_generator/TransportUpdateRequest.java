package org.microarchitecturovisco.transport.model.dto.data_generator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportUpdateRequest {

    private DataUpdateType updateType;

    private UUID id;

    private UUID courseId;

    private UUID courseLocationFromId;
    private UUID courseLocationToId;

    private LocalDateTime departureDate;

    private int capacity;

    private float pricePerAdult;
}

