package org.microarchitecturovisco.transport.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    private TransportCourse course;

    private Date departureDate;

    private Date returnDate;

    @Enumerated(EnumType.STRING)
    private TransportType type;

    private int capacity;

    private float pricePerAdult;
}
