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
@Table(name="rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(targetEntity = Hotel.class)
    @JoinColumn(name="hotel_id")
    private Hotel hotel;

    @NotNull
    private String name;

    @NotNull
    private int guestCapacity;

    @NotNull
    private float pricePerAdult;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy="room")
    private List<RoomReservation> roomReservations;
}
