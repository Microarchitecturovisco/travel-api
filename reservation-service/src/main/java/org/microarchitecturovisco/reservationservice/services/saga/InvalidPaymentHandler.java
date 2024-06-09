package org.microarchitecturovisco.reservationservice.services.saga;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.HotelReservationDeleteRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.ReservationRequest;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.TransportReservationDeleteRequest;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.queues.config.QueuesReservationConfig;
import org.microarchitecturovisco.reservationservice.repositories.ReservationRepository;
import org.microarchitecturovisco.reservationservice.utils.json.JsonConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class InvalidPaymentHandler {
    private final BookHotelsSaga bookHotelsSaga;
    private final BookTransportsSaga bookTransportsSaga;
    private final ReservationRepository reservationRepository;
    public static final int PAYMENT_TIMEOUT_SECONDS = 60;
    public static Logger logger = Logger.getLogger(InvalidPaymentHandler.class.getName());
    private final RabbitTemplate rabbitTemplate;

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
            logger.info("RESERVATION OBJ:" + reservation);
            if(!reservation.isPaid()){
                logger.warning("PAYMENT TIMEOUT FOR ID: " + reservationRequest.getId() + " !");
                rollbackReservation(reservationRequest);
            }
            else {
                logger.warning("Reservation with ID: " + reservationRequest.getId() + " was registered as 'paid successfully'");
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
        String requestJson = JsonConverter.convert(reservationRequest);

        logger.info("Deleting reservation object: " + requestJson);

        rabbitTemplate.convertAndSend(
                QueuesReservationConfig.EXCHANGE_DELETE_RESERVATION,
                "", // Routing key is ignored for FanoutExchange
                requestJson
        );
    }


}
