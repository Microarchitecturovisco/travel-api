package org.microarchitecturovisco.transport.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
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

    public Transport() {
    }

    public Transport(TransportCourse course, Date departureDate, Date returnDate, TransportType type, int capacity, float pricePerAdult) {
        this.course = course;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.type = type;
        this.capacity = capacity;
        this.pricePerAdult = pricePerAdult;
    }

}
