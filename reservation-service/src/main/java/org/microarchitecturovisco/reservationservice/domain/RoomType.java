package org.microarchitecturovisco.reservationservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int guestCapacity;

    private String name;

    private float pricePerAdult;

    private String description;

    @ManyToOne
    @JoinColumn(name="reservation_id")
    private Reservation reservation;
}
