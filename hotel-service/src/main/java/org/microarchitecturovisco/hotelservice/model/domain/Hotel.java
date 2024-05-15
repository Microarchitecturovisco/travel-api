package org.microarchitecturovisco.hotelservice.model.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hotel {
    @Id
    private UUID id;

    private String name;

    private float rating;

    private String description;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<CateringOption> cateringOptions;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<Room> rooms;

    @ElementCollection
    private List<String> photos;
}
