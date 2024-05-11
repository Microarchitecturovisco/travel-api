package org.microarchitecturovisco.hotelservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="catering_options")
public class CateringOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CateringType type;

    private float rating;

    @NotNull
    private float price;

    @ManyToOne()
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

}

