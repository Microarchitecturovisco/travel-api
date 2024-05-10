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
@Table(name="locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    private String country;

    private String region;

    @OneToMany(mappedBy = "location")
    private List<Hotel> hotel;

    public Location(String country, String region) {
        this.country = country;
        this.region = region;
    }
}