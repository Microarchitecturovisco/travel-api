package org.microarchitecturovisco.hotelservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="hotel_id")
    private Hotel hotel;

    @NotNull
    private String name;

    @NotNull
    private int guestCapacity;

    @NotNull
    private float pricePerAdult;

    private String description;

    @OneToMany(mappedBy="room", cascade = CascadeType.ALL)
    private List<RoomReservation> roomReservations;
}
