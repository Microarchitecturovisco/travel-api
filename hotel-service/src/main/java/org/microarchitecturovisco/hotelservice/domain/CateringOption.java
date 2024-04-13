package org.microarchitecturovisco.hotelservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CateringOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private CateringType type;

    private float rating;

    private float price;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

}

