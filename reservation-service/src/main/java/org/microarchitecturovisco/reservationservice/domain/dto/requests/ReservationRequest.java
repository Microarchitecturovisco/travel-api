package org.microarchitecturovisco.reservationservice.domain.dto.requests;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest implements Serializable {
    private UUID id;

    private LocalDateTime hotelTimeFrom;

    private LocalDateTime hotelTimeTo;

    private int adultsQuantity;

    private int childrenUnder3Quantity;

    private int childrenUnder10Quantity;

    private int childrenUnder18Quantity;

    private float price;
    private UUID hotelId;
    private List<UUID> roomReservationsIds;
    private List<UUID> transportReservationsIds;
    private UUID userId;

    private List<String> roomReservationsNames;
    private String locationNameFrom;
    private String locationNameTo;

}
