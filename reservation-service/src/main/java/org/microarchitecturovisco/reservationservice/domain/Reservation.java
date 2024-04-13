package org.microarchitecturovisco.reservationservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private ReservationTransport reservationTransport;

    private Date hotelTimeFrom;

    private Date hotelTimeTo;

    private int infantsQuantity;
    private int kidsQuantity;
    private int teensQuantity;
    private int adultsQuantity;

    @OneToMany(mappedBy = "reservation")
    private List<RoomType> rooms;

    private float price;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
