package org.microarchitecturovisco.hotelservice.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int guestCapacity;

    private String name;

    private float pricePerAdult;

    private String description;

    @OneToOne
    @JoinColumn(name = "hotelrooms_id")
    private HotelRooms hotelRooms;

}