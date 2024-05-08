package org.microarchitecturovisco.reservationservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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
    private int hotelId;

    @NotNull
    @ElementCollection
    private Collection<Integer> roomReservationsIds;

    @NotNull
    private int userId;
}
