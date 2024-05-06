package org.microarchitecturovisco.hotelservice.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelRooms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int availableQuantity;

    @ManyToOne
    @JoinColumn(name="hotel_id")
    private Hotel hotel;

    @OneToOne(mappedBy = "hotelRooms")
    private RoomType roomType;
}
