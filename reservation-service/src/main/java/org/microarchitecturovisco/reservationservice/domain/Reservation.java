package org.microarchitecturovisco.reservationservice.domain;

import jakarta.persistence.*;
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

    private LocalDateTime hotelTimeFrom;

    private LocalDateTime hotelTimeTo;

    private int infantsQuantity;

    private int kidsQuantity;

    private int teensQuantity;

    private int adultsQuantity;

    private float price;

    private boolean paid;

    private int hotelId;

    @ElementCollection
    private Collection<Integer> roomReservationsIds;

    private int userId;
}
