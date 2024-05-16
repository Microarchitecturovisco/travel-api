package org.microarchitecturovisco.reservationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.queues.hotels.ReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.queues.config.QueuesConfig;
import org.microarchitecturovisco.reservationservice.services.ReservationService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ReservationService reservationService;

    @PostMapping("/reservation")
    public String addReservation(@RequestBody ReservationRequest reservationRequest) {
        reservationService.bookAndBuyOrchestration(reservationRequest);
        return "sent success";
    }

    @GetMapping("/test")
    public Reservation test() {
        return reservationService.createReservation(LocalDateTime.now(), LocalDateTime.now(), 1, 1, 0, 2,
                9642.01f, 1, List.of(), List.of(), 1);
    }

}
