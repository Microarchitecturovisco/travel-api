package org.microarchitecturovisco.reservationservice.domain.events;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationCreatedEvent extends ReservationEvent {
    private String idReservation;
    private LocalDateTime hotelTimeFrom;
    private LocalDateTime hotelTimeTo;
    private int infantsQuantity;
    private int kidsQuantity;
    private int teensQuantity;
    private int adultsQuantity;
    private float price;
    private boolean paid;
    private int hotelId;
    @ElementCollection
    private List<String> roomReservationsIds;
    @ElementCollection
    private List<String> transportReservationsIds;
    private int userId;
}
