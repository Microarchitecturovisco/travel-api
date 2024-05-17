package org.microarchitecturovisco.reservationservice.queues.hotels;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReservationRequest {
    private LocalDateTime hotelTimeFrom;

    private LocalDateTime hotelTimeTo;

    private int adultsQuantity;

    private int childrenUnder3Quantity;

    private int childrenUnder10Quantity;

    private int childrenUnder18Quantity;

    private List<UUID> departureLocationIdsByPlane;
    private List<UUID> departureLocationIdsByBus;
    private List<UUID> arrivalLocationIds;

    private float price;
    private UUID hotelId;
    private List<UUID> roomReservationsIds;
    private List<UUID> transportReservationsIds;
    private UUID userId;
    private UUID reservationId;
}
