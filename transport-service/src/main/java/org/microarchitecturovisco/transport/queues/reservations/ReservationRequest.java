package org.microarchitecturovisco.transport.queues.reservations;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReservationRequest {
    private UUID hotelId;

    private LocalDateTime hotelTimeFrom;

    private LocalDateTime hotelTimeTo;

    private int adultsQuantity;

    private int childrenUnder3Quantity;

    private int childrenUnder10Quantity;

    private int childrenUnder18Quantity;

    private List<String> roomIds;

    private int userId;

    private List<String> departureLocationIdsByPlane;
    private List<String> departureLocationIdsByBus;
    private List<String> arrivalLocationIds;
}