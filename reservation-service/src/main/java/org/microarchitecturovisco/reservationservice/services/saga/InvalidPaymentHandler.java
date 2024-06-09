package org.microarchitecturovisco.reservationservice.services.saga;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.commands.DeleteReservationCommand;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.HotelReservationDeleteRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.ReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.TransportReservationDeleteRequest;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.repositories.ReservationRepository;
import org.microarchitecturovisco.reservationservice.services.ReservationAggregate;
import org.microarchitecturovisco.reservationservice.services.ReservationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class InvalidPaymentHandler {
    private final BookHotelsSaga bookHotelsSaga;
    private final BookTransportsSaga bookTransportsSaga;
    private final ReservationAggregate reservationAggregate;
    private final ReservationRepository reservationRepository;
    public static final int PAYMENT_TIMEOUT_SECONDS = 60;
    public static Logger logger = Logger.getLogger(InvalidPaymentHandler.class.getName());

    public void schedulePaymentTimeoutCheck(ReservationRequest reservationRequest){
        Runnable paymentTimeoutRunnable = () -> {
            paymentTimeout(reservationRequest);
        };
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(paymentTimeoutRunnable, PAYMENT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private void paymentTimeout(ReservationRequest reservationRequest) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationRequest.getId());

        if(reservationOptional.isPresent()){
            Reservation reservation = reservationOptional.get();
            if(!reservation.isPaid()){
                ReservationService.logger.warning("PAYMENT TIMEOUT FOR ID: " + reservationRequest.getId() + " !");
                rollbackReservation(reservationRequest);
            }
            else {
                ReservationService.logger.warning("Reservation with ID: " + reservationRequest.getId() + " was registered as 'paid successfully'");
            }
        }
    }

    public void rollbackReservation(ReservationRequest reservationRequest){
        // Delete reservation in Transport service
        rollbackForTransportReservation(reservationRequest);

        // Delete reservation in Hotel service
        rollbackForHotelReservation(reservationRequest);

        // Delete reservation from the ReservationRepository in Reservation service
        rollbackForReservationObject(reservationRequest);
    }

    private void rollbackForTransportReservation(ReservationRequest reservationRequest) {
        TransportReservationDeleteRequest transportReservationDeleteRequest = TransportReservationDeleteRequest.builder()
                .transportReservationsIds(reservationRequest.getTransportReservationsIds())
                .reservationId(reservationRequest.getId())
                .build();
        bookTransportsSaga.deleteTransportReservation(transportReservationDeleteRequest);
    }

    private void rollbackForHotelReservation(ReservationRequest reservationRequest) {
        HotelReservationDeleteRequest hotelReservationDeleteRequest = HotelReservationDeleteRequest.builder()
                .hotelId(reservationRequest.getHotelId())
                .reservationId(reservationRequest.getId())
                .roomIds(reservationRequest.getRoomReservationsIds())
                .build();
        bookHotelsSaga.deleteHotelReservation(hotelReservationDeleteRequest);
    }

    private void rollbackForReservationObject(ReservationRequest reservationRequest) {
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
