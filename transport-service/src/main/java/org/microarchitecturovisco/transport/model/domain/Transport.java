package org.microarchitecturovisco.transport.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.repository.cdi.Eager;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    private TransportCourse course;

    @NotNull
    private LocalDateTime departureDate;

    @NotNull
    private int capacity;

    @NotNull
    private float pricePerAdult;

    @OneToMany(mappedBy="transport")
    private List<TransportReservation> transportReservations;


    public Transport(TransportCourse course, LocalDateTime departureDate, int capacity, float pricePerAdult) {
        this.course = course;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.pricePerAdult = pricePerAdult;
    }
}
