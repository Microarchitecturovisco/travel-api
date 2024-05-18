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
@Getter
@Setter
public class CreateTransportReservationRequest {
    private UUID reservationId;

    private LocalDateTime hotelTimeFrom;

    private LocalDateTime hotelTimeTo;
    private int adultsQuantity;
    private int childrenUnder3Quantity;
    private int childrenUnder10Quantity;
    private int childrenUnder18Quantity;

    private List<UUID> transportReservationsIds;
}