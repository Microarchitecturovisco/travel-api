package org.microarchitecturovisco.hotelservice.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="hotel_id")
    private Hotel hotel;

    private String name;

    private int guestCapacity;

    private float pricePerAdult;

    private String description;
}
