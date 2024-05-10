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

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "hotel")
    private List<CateringOption> cateringOptions;

    @ManyToOne()
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "hotel")
    private List<Room> rooms;

    @ElementCollection
    private List<String> photos;

    public Hotel(int id,
                 String name,
                 String description,
                 float rating,
                 Location location,
                 List<String> photos) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.description = description;
        this.location = location;
        this.photos = photos;
    }
}
