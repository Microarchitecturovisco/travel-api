package org.microarchitecturovisco.reservationservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="reservation_id")
    private Reservation reservation;

    @OneToOne
    @JoinColumn(name="room_type_id")
    private RoomType roomType;

    private int numberOfGuests;

}
