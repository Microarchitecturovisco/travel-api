package org.microarchitecturovisco.reservationservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.commands.CreateReservationCommand;
import org.microarchitecturovisco.reservationservice.domain.commands.DeleteReservationCommand;
import org.microarchitecturovisco.reservationservice.domain.commands.UpdateReservationCommand;
import org.microarchitecturovisco.reservationservice.domain.dto.HotelInfo;
import org.microarchitecturovisco.reservationservice.domain.dto.PaymentRequestDto;
import org.microarchitecturovisco.reservationservice.domain.dto.PaymentResponseDto;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.domain.exceptions.PaymentProcessException;
import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationFailException;
import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationNotFoundAfterPaymentException;
import org.microarchitecturovisco.reservationservice.domain.model.LocationReservationResponse;
import org.microarchitecturovisco.reservationservice.domain.model.ReservationConfirmationResponse;
import org.microarchitecturovisco.reservationservice.domain.model.TransportReservationResponse;
import org.microarchitecturovisco.reservationservice.queues.config.HotelReservationDeleteRequest;
import org.microarchitecturovisco.reservationservice.queues.config.QueuesReservationConfig;
import org.microarchitecturovisco.reservationservice.domain.dto.requests.ReservationRequest;
import org.microarchitecturovisco.reservationservice.repositories.ReservationRepository;
import org.microarchitecturovisco.reservationservice.services.saga.BookHotelsSaga;
import org.microarchitecturovisco.reservationservice.services.saga.BookTransportsSaga;
import org.microarchitecturovisco.reservationservice.utils.json.JsonConverter;
import org.microarchitecturovisco.reservationservice.utils.json.JsonReader;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ReservationService {

    public static final int PAYMENT_TIMEOUT_SECONDS = 10;

    public static Logger logger = Logger.getLogger(ReservationService.class.getName());

    private final ReservationRepository reservationRepository;
    private final ReservationAggregate reservationAggregate;

    private final BookHotelsSaga bookHotelsSaga;
    private final BookTransportsSaga bookTransportsSaga;

    private final RabbitTemplate rabbitTemplate;

    public Reservation createReservation(LocalDateTime hotelTimeFrom, LocalDateTime hotelTimeTo,
                                                      int infantsQuantity, int kidsQuantity, int teensQuantity, int adultsQuantity,
                                                      float price, UUID hotelId, List<UUID> roomReservationsIds,
                                                      List<UUID> transportReservationsIds, UUID userId, UUID reservationId) {

        CreateReservationCommand command = CreateReservationCommand.builder()
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
        reservationAggregate.handleCreateReservationCommand(command);
        return reservationRepository.findById(reservationId).orElseThrow(RuntimeException::new);
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


    public UUID bookOrchestration(ReservationRequest reservationRequest) throws ReservationFailException {

        checkHotelAvailability(reservationRequest);

        checkTransportAvailability(reservationRequest);

        UUID reservationId = UUID.randomUUID();
        reservationRequest.setId(reservationId);
        createReservationFromRequest(reservationRequest);

        bookHotelsSaga.createHotelReservation(reservationRequest);

        bookTransportsSaga.createTransportReservation(reservationRequest);

        Runnable paymentTimeoutRunnable = () -> {
            paymentTimeout(reservationRequest);
        };
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(paymentTimeoutRunnable, PAYMENT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        return null; // reservationId
    }

    private void checkHotelAvailability(ReservationRequest reservationRequest) throws ReservationFailException {
        boolean hotelIsAvailable = bookHotelsSaga.checkIfHotelIsAvailable(reservationRequest);
//        boolean hotelIsAvailable = true; // debug only
        System.out.println("hotelIsAvailable: "+ hotelIsAvailable);
        if(!hotelIsAvailable) { throw new ReservationFailException(); }
    }

    private void checkTransportAvailability(ReservationRequest reservationRequest) throws ReservationFailException {
//        boolean transportIsAvailable = bookTransportsSaga.checkIfTransportIsAvailable(reservationRequest);
        boolean transportIsAvailable = true; // debug only
        System.out.println("transportIsAvailable: " + transportIsAvailable);
        if(!transportIsAvailable) { throw new ReservationFailException(); }
    }

    public void createReservationFromRequest(ReservationRequest reservationRequest) {
        String reservationRequestJson = JsonConverter.convert(reservationRequest);

        System.out.println("reservationRequestJson: " + reservationRequestJson);

        rabbitTemplate.convertAndSend(QueuesReservationConfig.EXCHANGE_RESERVATION, "", reservationRequestJson);

        System.out.println("reservationCreated: " + reservationRequest.getId());
    }

    public void paymentTimeout(ReservationRequest reservationRequest) {
        ReservationService.logger.warning("PAYMENT TIMEOUT FOR ID: " + reservationRequest.getId() + " !");

        // Delete reservation in Transport service



        // Delete reservation in Hotel service
        HotelReservationDeleteRequest hotelReservationDeleteRequest = HotelReservationDeleteRequest.builder()
                .hotelId(reservationRequest.getHotelId())
                .reservationId(reservationRequest.getId())
                .roomIds(reservationRequest.getRoomReservationsIds())
                .build();
        bookHotelsSaga.deleteHotelReservation(hotelReservationDeleteRequest);

        // Delete reservation from the ReservationRepository in Reservation service
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

    public ReservationConfirmationResponse purchaseReservation(String reservationId, String cardNumber) {
        boolean successfulPayment = false;
        try {
            successfulPayment = processPaymentWithPaymentModule(reservationId, cardNumber);
        } catch (PaymentProcessException e) {
            logger.warning("Exception thrown in payment process:" + e.getMessage());
        }

        // ROLLBACK here
        if(!successfulPayment) {
            throw new RuntimeException(); //temporary
        }

        reservationAggregate.handleReservationUpdateCommand(UpdateReservationCommand.builder().reservationId(UUID.fromString(reservationId)).paid(true).build());
        Reservation reservation = reservationRepository.findById(UUID.fromString(reservationId)).orElseThrow(ReservationNotFoundAfterPaymentException::new);
        HotelInfo hotelInfo = getHotelInformation(reservation.getHotelId());
        TransportReservationResponse transportInfo = getTransportInformation(reservation.getTransportReservationsIds().stream().map(UUID::toString).toList());

        return ReservationConfirmationResponse.builder()
                .hotelName(hotelInfo.getName())
                .price(reservation.getPrice())
                .dateFrom(reservation.getHotelTimeFrom())
                .dateTo(reservation.getHotelTimeTo())
                .adults(reservation.getAdultsQuantity())
                .infants(reservation.getChildrenUnder3Quantity())
                .kids(reservation.getChildrenUnder10Quantity())
                .teens(reservation.getChildrenUnder18Quantity())
                .roomTypes(hotelInfo.getRoomTypes())
                .transport(transportInfo)
                .build();
    }

    private HotelInfo getHotelInformation(UUID hotelId) {
        return HotelInfo.builder().hotelPrice(1500.0f).name("Hotel testowy").roomTypes(Map.of("Pokój dwuosobowy", 1)).build();
    }

    private TransportReservationResponse getTransportInformation(List<String> transportIds) {
        return TransportReservationResponse.builder().type("Plane").departureFrom(LocationReservationResponse.builder().country("Polska").region("Gdańsk").build()).departureTo(LocationReservationResponse.builder().region("Marsa Alam").country("Egipt").build()).build();
    }

    private boolean processPaymentWithPaymentModule(String reservationId, String cardNumber) throws PaymentProcessException {
        PaymentRequestDto requestDto = PaymentRequestDto.builder()
                .idReservation(reservationId)
                .cardNumber(cardNumber)
                .build();

        String transportMessageJson = JsonConverter.convert(requestDto);
        try {
            String responseMessage = (String) rabbitTemplate.convertSendAndReceive("payments.requests.handle", "payments.requests.handle", transportMessageJson);

            if(responseMessage != null) {

                System.out.println("Transports message: " + responseMessage);

                PaymentResponseDto paymentResponseDto = JsonReader.readDtoFromJson(responseMessage, PaymentResponseDto.class);
                if(paymentResponseDto.getReservationId().equals(reservationId)) {
                    throw new PaymentProcessException("Requested payment id is different than returned from payment module");
                }
                if(!paymentResponseDto.isTransactionApproved()) {
                    throw new PaymentProcessException("Transaction was not approved");
                }

                return true;
            }
            else {
                throw new PaymentProcessException("Null message at: purchaseReservation()");
            }

        }
        catch (AmqpException e) {
            throw new PaymentProcessException("Amqp exception was thrown");
        }
    }

}
