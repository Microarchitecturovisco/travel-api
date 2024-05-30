package org.microarchitecturovisco.reservationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.ReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationFailException;
import org.microarchitecturovisco.reservationservice.domain.model.PurchaseRequestBody;
import org.microarchitecturovisco.reservationservice.domain.model.ReservationConfirmationResponse;
import org.microarchitecturovisco.reservationservice.services.ReservationService;
import org.microarchitecturovisco.reservationservice.utils.json.JsonReader;
import org.microarchitecturovisco.reservationservice.websockets.ReservationWebSocketHandlerBooking;
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
    private final ReservationWebSocketHandlerBooking reservationWebSocketHandlerBooking;

    @PostMapping("/reservation")
    public String addReservation(@RequestBody ReservationRequest reservationRequest) {
        try {
            System.out.println("RESERVATION REQUEST:" + reservationRequest.toString());
            UUID reservationId = reservationService.bookOrchestration(reservationRequest);
            return "Reservation with id " + reservationId.toString() + " created successfully!";
        } catch (ReservationFailException exception) {
            return "ReservationFailException exception occurred";
        }
    }

    @PostMapping("/purchase")
    public ReservationConfirmationResponse purchase(@RequestBody PurchaseRequestBody requestBody) {
        return reservationService.purchaseReservation(requestBody.getReservationId(), requestBody.getCardNumber());
    }

    @RabbitListener(queues = "#{handleReservationCreateQueue.name}")
    public void consumeMessageCreateReservation(String reservationRequestJson) {

        ReservationRequest reservationRequest = JsonReader.readDtoFromJson(reservationRequestJson, ReservationRequest.class);

        sendBookingOfferWebsocketMessages(reservationRequest);

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
        System.out.println("Reservation in Reservation module created successfully: " + reservation.getId());
    }

    private void sendBookingOfferWebsocketMessages(ReservationRequest reservationRequest) {
        String hotelName = "hotelName: " + reservationRequest.getHotelName();
        String roomNames = "roomNames: " + reservationRequest.getRoomReservationsNames();
        String locationNameFrom = "locationNameFrom: " + reservationRequest.getLocationNameFrom();
        String locationNameTo = "locationNameTo: " + reservationRequest.getLocationNameTo();
        String transportType = "transportType: " + reservationRequest.getTransportType();

        String message = String.join(" | ", hotelName, roomNames, locationNameFrom, locationNameTo, transportType);

        reservationWebSocketHandlerBooking.sendMessageToUI("Booked: " + message);
    }
}
