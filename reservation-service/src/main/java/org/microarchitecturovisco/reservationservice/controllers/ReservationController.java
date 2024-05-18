package org.microarchitecturovisco.reservationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationFailException;
import org.microarchitecturovisco.reservationservice.queues.hotels.ReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.services.ReservationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservation")
    public String addReservation(@RequestBody ReservationRequest reservationRequest) {
        try {
            UUID reservationId = reservationService.bookOrchestration(reservationRequest);
        } catch (ReservationFailException exception) {
            return "ReservationFailException exception occurred";
        }
        return "FULL SUCCESS";
    }

    @GetMapping("/test")
    public Reservation test() {
        return reservationService.createReservation(LocalDateTime.now(),
                LocalDateTime.now(),
                1,
                1,
                0,
                2,
                9642.01f,
                UUID.randomUUID(),
                List.of(),
                List.of(),
                UUID.randomUUID(),
                UUID.randomUUID());
    }

    @RabbitListener(queues = "#{handleReservationCreateQueue.name}")
    public void consumeMessageCreateReservation(ReservationRequest reservationRequest) {
        Reservation reservation = reservationService.createReservation(
                reservationRequest.getHotelTimeFrom(),
                reservationRequest.getHotelTimeTo(),
                reservationRequest.getChildrenUnder3Quantity(),
                reservationRequest.getChildrenUnder10Quantity(),
                reservationRequest.getChildrenUnder18Quantity(),
                reservationRequest.getAdultsQuantity(),
                reservationRequest.getPrice(),
                reservationRequest.getHotelId(),
                reservationRequest.getRoomReservationsIds(),
                reservationRequest.getTransportReservationsIds(),
                reservationRequest.getUserId(),
                reservationRequest.getId()
        );
        System.out.println("Reservation created successfully: " + reservation.getId());
    }
}