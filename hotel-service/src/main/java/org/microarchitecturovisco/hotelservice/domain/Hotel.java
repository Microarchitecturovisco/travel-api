package org.microarchitecturovisco.hotelservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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
