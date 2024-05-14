package org.microarchitecturovisco.reservationservice.domain.events;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;

import java.time.LocalDateTime;
import java.util.Collection;

@Builder
@Getter
@Setter
public class ReservationCreatedEvent extends ReservationEvent {
    private String id;
    private LocalDateTime hotelTimeFrom;
    private LocalDateTime hotelTimeTo;
    private int infantsQuantity;
    private int kidsQuantity;
    private int teensQuantity;
    private int adultsQuantity;
    private float price;
    private boolean paid;
    private int hotelId;
    private Collection<Integer> roomReservationsIds;
    private Collection<Integer> transportReservationsIds;
    private int userId;
}
