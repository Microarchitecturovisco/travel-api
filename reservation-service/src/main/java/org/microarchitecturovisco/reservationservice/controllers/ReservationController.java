package org.microarchitecturovisco.reservationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.commands.DeleteReservationCommand;
import org.microarchitecturovisco.reservationservice.domain.dto.ReservationPreference;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.ReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationFailException;
import org.microarchitecturovisco.reservationservice.domain.model.PurchaseRequestBody;
import org.microarchitecturovisco.reservationservice.domain.model.ReservationConfirmationResponse;
import org.microarchitecturovisco.reservationservice.services.ReservationAggregate;
import org.microarchitecturovisco.reservationservice.services.ReservationService;
import org.microarchitecturovisco.reservationservice.utils.json.JsonReader;
import org.microarchitecturovisco.reservationservice.websockets.ReservationWebSocketHandlerPreferences;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationWebSocketHandlerPreferences reservationWebSocketHandlerPreferences;
    private final ReservationAggregate reservationAggregate;
    public static Logger logger = Logger.getLogger(ReservationController.class.getName());

    @PostMapping("/reservation")
    public String addReservation(@RequestBody ReservationRequest reservationRequest) {
        try {
            logger.info("RESERVATION REQUEST:" + reservationRequest.toString());
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

        updateBookingPreferences(reservationRequest);

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
        logger.info("Reservation in Reservation module created successfully: " + reservation.getId());
    }

    private void updateBookingPreferences(ReservationRequest reservationRequest) {

        ReservationPreference reservationPreference = ReservationPreference.builder()
                .hotelName(reservationRequest.getHotelName())
                .roomReservationsNames(reservationRequest.getRoomReservationsNames())
                .locationFromNameRegionAndCountry(reservationRequest.getLocationFromNameRegionAndCountry())
                .locationToNameRegionAndCountry(reservationRequest.getLocationToNameRegionAndCountry())
                .transportType(reservationRequest.getTransportType())
                .reservationTime(getCurrentTime())
                .build();

        reservationWebSocketHandlerPreferences.updateReservationPreferences(reservationPreference);
    }

    private String getCurrentTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
    
    @RabbitListener(queues = "#{handleReservationDeleteQueue.name}")
    public void consumeMessageDeleteReservation(String reservationRequestJson) {

        ReservationRequest reservationRequest = JsonReader.readDtoFromJson(reservationRequestJson, ReservationRequest.class);

        deleteReservation(
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

        logger.info("Reservation in Reservation module deleted successfully");
    }

    private void deleteReservation(LocalDateTime hotelTimeFrom, LocalDateTime hotelTimeTo,
                                   int infantsQuantity, int kidsQuantity, int teensQuantity, int adultsQuantity,
                                   float price, UUID hotelId, List<UUID> roomReservationsIds,
                                   List<UUID> transportReservationsIds, UUID userId, UUID reservationId) {

        DeleteReservationCommand command = DeleteReservationCommand.builder()
                .id(reservationId)
                .hotelTimeFrom(hotelTimeFrom)
                .hotelTimeTo(hotelTimeTo)
                .infantsQuantity(infantsQuantity)
                .kidsQuantity(kidsQuantity)
                .teensQuantity(teensQuantity)
                .adultsQuantity(adultsQuantity)
                .price(price)
                .paid(false)
                .hotelId(hotelId)
                .roomReservationsIds(roomReservationsIds)
                .transportReservationsIds(transportReservationsIds)
                .userId(userId)
                .build();
        reservationAggregate.handleDeleteReservationCommand(command);
    }
}
