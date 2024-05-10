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
@Table(name="hotels")
public class Hotel {
    @Id
    private int id;

    private String name;

    private float rating;

    private String description;

    @OneToMany(mappedBy = "hotel")
    private List<CateringOption> cateringOptions;

    @ManyToOne()
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<Room> rooms;

    @ElementCollection
    private List<String> photos;

    // Constructor with all fields except id
    public Hotel(int id,
                 String name,
                 float rating,
                 String description,
                 Location location) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.description = description;
        this.location = location;
    }
}
