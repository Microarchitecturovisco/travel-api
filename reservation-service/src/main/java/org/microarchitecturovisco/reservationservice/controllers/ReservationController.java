package org.microarchitecturovisco.reservationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationFailException;
import org.microarchitecturovisco.reservationservice.queues.config.QueuesReservationConfig;
import org.microarchitecturovisco.reservationservice.queues.hotels.ReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.services.ReservationService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ReservationService reservationService;

    @PostMapping("/reservation")
    public String addReservation(@RequestBody ReservationRequest reservationRequest) {
        try{
            UUID reservationId = reservationService.bookOrchestration(reservationRequest);
        }
        catch (ReservationFailException exception){
            return "FAILED";
        }
        return "sent success";
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

    @RabbitListener(queues = QueuesReservationConfig.QUEUE_RESERVATION_CREATE_REQ)
    public void consumeMessageCreateReservation(ReservationRequest reservationRequest) {
        System.out.println("COPY Message received: " + reservationRequest);
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
                reservationRequest.getReservationId()
        );
        System.out.println("COPY: Reservation after creation: " + reservation.getId());
    }


}
