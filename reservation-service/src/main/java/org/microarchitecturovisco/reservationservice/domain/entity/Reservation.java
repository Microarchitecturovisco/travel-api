package org.microarchitecturovisco.reservationservice.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;
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

    private int infantsQuantity;

    private int kidsQuantity;

    private int teensQuantity;

    @NotNull
    private int adultsQuantity;

    @NotNull
    private float price;

    @NotNull
    private boolean paid;

    @NotNull
    private UUID hotelId;

    @NotNull
    @ElementCollection
    private List<UUID> roomReservationsIds;

    @NotNull
    @ElementCollection
    private List<UUID> transportReservationsIds;

    @NotNull
    private UUID userId;
}
