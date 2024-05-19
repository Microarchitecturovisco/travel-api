package org.microarchitecturovisco.reservationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationFailException;
import org.microarchitecturovisco.reservationservice.domain.model.PurchaseRequestBody;
import org.microarchitecturovisco.reservationservice.domain.model.ReservationConfirmationResponse;
import org.microarchitecturovisco.reservationservice.queues.config.ReservationRequest;
import org.microarchitecturovisco.reservationservice.services.ReservationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
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

    @PostMapping("/purchase")
    public ReservationConfirmationResponse purchase(@RequestBody PurchaseRequestBody requestBody) {
        return reservationService.purchaseReservation(requestBody.getReservationId(), requestBody.getCardNumber());
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
