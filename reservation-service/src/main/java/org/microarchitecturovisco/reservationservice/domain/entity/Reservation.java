package org.microarchitecturovisco.reservationservice.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Reservation {

    @Id
    private UUID id;

    @NotNull
    private LocalDateTime hotelTimeFrom;

    @NotNull
    private LocalDateTime hotelTimeTo;

    private int childrenUnder3Quantity;

    private int childrenUnder10Quantity;

    private int childrenUnder18Quantity;

    @NotNull
    private int adultsQuantity;

    @NotNull
    private float price;

    @NotNull
    private boolean paid;

    @NotNull
    private String hotelId;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private List<UUID> roomReservationsIds;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private List<UUID> transportReservationsIds;

    @NotNull
    private UUID userId;
}
