package org.microarchitecturovisco.reservationservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int guestCapacity;

    private String name;

    private float pricePerAdult;

    private String description;

    @OneToOne(mappedBy = "roomType")
    private RoomReservation roomReservation;
}
