package org.microarchitecturovisco.reservationservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.commands.CreateReservationCommand;
import org.microarchitecturovisco.reservationservice.domain.commands.UpdateReservationCommand;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.domain.events.ReservationCreatedEvent;
import org.microarchitecturovisco.reservationservice.domain.events.ReservationEvent;
import org.microarchitecturovisco.reservationservice.domain.events.ReservationUpdateEvent;
import org.microarchitecturovisco.reservationservice.repositories.ReservationEventStore;
import org.microarchitecturovisco.reservationservice.repositories.ReservationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Component
public class ReservationAggregate {
    static Logger logger = Logger.getLogger("ReservationAggregate");
    private final ReservationProjector reservationProjector;
    private final ReservationEventStore reservationEventStore;
    private final ReservationRepository reservationRepository;

    public List<ReservationEvent> handleCreateReservationCommand(CreateReservationCommand command) {
        ReservationCreatedEvent event = ReservationCreatedEvent.builder()
                .id(command.getId())
                .hotelTimeFrom(command.getHotelTimeFrom())
                .hotelTimeTo(command.getHotelTimeTo())
                .infantsQuantity(command.getInfantsQuantity())
                .kidsQuantity(command.getKidsQuantity())
                .teensQuantity(command.getTeensQuantity())
                .adultsQuantity(command.getAdultsQuantity())
                .price(command.getPrice())
                .paid(command.isPaid())
                .hotelId(command.getHotelId())
                .roomReservationsIds(command.getRoomReservationsIds())
                .transportReservationsIds(command.getTransportReservationsIds())
                .userId(command.getUserId())
                .build();
        reservationEventStore.save(event);
        reservationProjector.project(null, List.of(event));
        return List.of(event);
    }

    public List<ReservationEvent> handleReservationUpdateCommand(UpdateReservationCommand command) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(command.getReservationId());
        if(reservationOptional.isEmpty()) {
            logger.warning("Reservation with id " + command.getReservationId() + " not found.");
            return null;
        }

        ReservationUpdateEvent event = ReservationUpdateEvent.builder()
                .id(command.getReservationId())
                .paid(command.isPaid())
                .build();
        reservationEventStore.save(event);
        reservationProjector.project(command.getReservationId(), List.of(event));

        return List.of(event);
    }
}
