package org.microarchitecturovisco.reservationservice.domain.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationRequest implements Serializable {
    private UUID id;

    private LocalDateTime hotelTimeFrom;

    private LocalDateTime hotelTimeTo;

    private int adultsQuantity;

    private int childrenUnder3Quantity;

    private int childrenUnder10Quantity;

    private int childrenUnder18Quantity;

    private TransportType transportType;

    private List<UUID> departureLocationIds;
    private List<UUID> arrivalLocationIds;

    private float price;
    private UUID hotelId;
    private List<UUID> roomReservationsIds;
    private List<UUID> transportReservationsIds;
    private UUID userId;
}
