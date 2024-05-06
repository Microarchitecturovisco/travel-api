package org.microarchitecturovisco.reservationservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToOne
    @JoinColumn(name = "reservation_transport_id")
    private ReservationTransport reservationTransport;

    private LocalDateTime hotelTimeFrom;

    private LocalDateTime hotelTimeTo;

    private int infantsQuantity;

    private int kidsQuantity;

    private int teensQuantity;

    private int adultsQuantity;

    @OneToMany(mappedBy = "reservation")
    private List<RoomReservation> rooms;

    private float price;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
