package org.microarchitecturovisco.hotelservice.model.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {
    @Id
    private UUID id;

    @ManyToOne()
    @JoinColumn(name="hotel_id")
    private Hotel hotel;

    @NotNull
    private String name;

    @NotNull
    private int guestCapacity;

    @NotNull
    private float pricePerAdult;
    @Lob
    private String description;

    @OneToMany(mappedBy="room", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RoomReservation> roomReservations;
}
