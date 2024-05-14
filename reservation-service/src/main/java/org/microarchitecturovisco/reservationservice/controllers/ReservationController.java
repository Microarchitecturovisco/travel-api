package org.microarchitecturovisco.reservationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.domain.model.CreateReservationRequest;
import org.microarchitecturovisco.reservationservice.services.ReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservation")
    public String addReservation(@RequestBody CreateReservationRequest request) {
        return "reservation added";
    }

    @GetMapping("/test")
    public Reservation test() {
        return reservationService.createReservation(LocalDateTime.now(), LocalDateTime.now(), 1, 1, 0, 2,
                9642.01f, 1, List.of(), List.of(), 1);
    }

}
