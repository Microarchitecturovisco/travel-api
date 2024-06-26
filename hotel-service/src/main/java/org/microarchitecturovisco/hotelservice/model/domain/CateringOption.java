package org.microarchitecturovisco.hotelservice.model.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CateringOption {
    @Id
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CateringType type;

    private float rating;

    @NotNull
    private float price;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

}

