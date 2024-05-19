package org.microarchitecturovisco.transport.controllers.reservations;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CheckTransportAvailabilityRequest {
    private LocalDateTime hotelTimeFrom;

    private LocalDateTime hotelTimeTo;

    private int adultsQuantity;

    private int childrenUnder3Quantity;

    private int childrenUnder10Quantity;

    private int childrenUnder18Quantity;

    private List<UUID> departureLocationIdsByPlane;
    private List<UUID> departureLocationIdsByBus;
    private List<UUID> arrivalLocationIds;
}
