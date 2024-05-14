package org.microarchitecturovisco.reservationservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.domain.events.ReservationCreatedEvent;
import org.microarchitecturovisco.reservationservice.domain.events.ReservationEvent;
import org.microarchitecturovisco.reservationservice.domain.events.ReservationUpdateEvent;
import org.microarchitecturovisco.reservationservice.repositories.ReservationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ReservationProjector {
    private final ReservationRepository reservationRepository;

    public void project(List<ReservationEvent> events) {
        for(ReservationEvent event : events) {
            if(event instanceof ReservationCreatedEvent) {
                apply((ReservationCreatedEvent) event);
            }
            if(event instanceof ReservationUpdateEvent) {
                apply((ReservationUpdateEvent) event);
            }
        }
    }

    public void apply(ReservationCreatedEvent event) {
        Reservation reservation = Reservation.builder()
                .id(event.getIdReservation())
                .hotelTimeFrom(event.getHotelTimeFrom())
                .hotelTimeTo(event.getHotelTimeTo())
                .infantsQuantity(event.getInfantsQuantity())
                .kidsQuantity(event.getKidsQuantity())
                .teensQuantity(event.getTeensQuantity())
                .adultsQuantity(event.getAdultsQuantity())
                .price(event.getPrice())
                .paid(event.isPaid())
                .hotelId(event.getHotelId())
                .roomReservationsIds(event.getRoomReservationsIds())
                .transportReservationsIds(event.getTransportReservationsIds())
                .userId(event.getUserId())
                .build();
        reservationRepository.save(reservation);
    }

    public void apply(ReservationUpdateEvent event) {
        Reservation reservation = reservationRepository.findById(event.getIdReservation()).orElseThrow(RuntimeException::new);
        reservation.setPaid(event.getPaid());

        reservationRepository.save(reservation);
    }


}
