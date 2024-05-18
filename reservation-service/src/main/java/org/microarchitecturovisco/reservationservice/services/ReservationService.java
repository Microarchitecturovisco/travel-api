package org.microarchitecturovisco.reservationservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.commands.CreateReservationCommand;
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
import org.microarchitecturovisco.reservationservice.queues.hotels.ReservationRequest;
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

    public static final int PAYMENT_TIMEOUT_SECONDS = 60;
    public static Logger logger = Logger.getLogger(ReservationService.class.getName());

    private final ReservationRepository reservationRepository;
    private final ReservationAggregate reservationAggregate;

    private final BookHotelsSaga bookHotelsSaga;
    private final BookTransportsSaga bookTransportsSaga;

    private final RabbitTemplate rabbitTemplate;

    public Reservation createReservation(LocalDateTime hotelTimeFrom, LocalDateTime hotelTimeTo,
                                         int infantsQuantity, int kidsQuantity, int teensQuantity, int adultsQuantity,
                                         float price, String hotelId, List<String> roomReservationsIds,
                                         List<String> transportReservationsIds, int userId) {
        String id = UUID.randomUUID().toString();

        CreateReservationCommand command = CreateReservationCommand.builder()
                .id(id)
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
        return reservationRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public UUID bookOrchestration(ReservationRequest reservationRequest) throws ReservationFailException {

        boolean hotelIsAvailable = bookHotelsSaga.checkIfHotelIsAvailable(reservationRequest);
        System.out.println("hotelIsAvailable: "+ hotelIsAvailable);

        if(!hotelIsAvailable) { throw new ReservationFailException(); }

        boolean transportIsAvailable = bookTransportsSaga.checkIfTransportIsAvailable(reservationRequest);
        if(!transportIsAvailable) { throw new ReservationFailException(); }


        //todo: Create Reservation here --> use createReservation()
        // Tworzony jest obiekt rezerwacji
        // Orkiestrator wysyła event stworzenia obiektu do kolejki reservations.events.createReservation
        // Reservations tworzy instancje



        // todo: reserve hotel
        //  Wysyłany jest event zarezerwowania hotelu do kolejki hotels.events.createHotelReservation



        // todo: reserve transport
        //  Wysyłany jest event zarezerwowania transportu do kolejki transports.events.createTransportReservation


        // todo: Rozpoczyna się odliczanie do przedawnienia się rezerwacji
        //  (co skutkuje cofnięciem poprzednich operacji);
        //  do aplikacji klienckiej zwracany jest status 2xx oraz idReservation tego zamówienia
        //  dodać pole Timestamp stworzenia rezerwacji do klasy Reservation


        // Tu jest nie dokończony kod, który stanowi podstawę pod obsługę płatności (reservationId będzie gdzieś z góry)

        String reservationId = UUID.randomUUID().toString();

        Runnable paymentTimeoutRunnable = () -> {
            paymentTimeout(reservationId);
        };
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(paymentTimeoutRunnable, PAYMENT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        return null; // Id rezerwacji
    }

    public void paymentTimeout(String reservationId) {
        ReservationService.logger.warning("PAYMENT TIMEOUT FOR ID: " + reservationId + " !");

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

        reservationAggregate.handleReservationUpdateCommand(UpdateReservationCommand.builder().reservationId(reservationId).paid(true).build());
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(ReservationNotFoundAfterPaymentException::new);
        HotelInfo hotelInfo = getHotelInformation(reservation.getHotelId());
        TransportReservationResponse transportInfo = getTransportInformation(reservation.getTransportReservationsIds());

        return ReservationConfirmationResponse.builder()
                .hotelName(hotelInfo.getName())
                .price(reservation.getPrice())
                .dateFrom(reservation.getHotelTimeFrom())
                .dateTo(reservation.getHotelTimeTo())
                .adults(reservation.getAdultsQuantity())
                .infants(reservation.getInfantsQuantity())
                .kids(reservation.getKidsQuantity())
                .teens(reservation.getTeensQuantity())
                .roomTypes(hotelInfo.getRoomTypes())
                .transport(transportInfo)
                .build();
    }

    private HotelInfo getHotelInformation(String hotelId) {
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

        String transportMessageJson = JsonConverter.convertToJsonWithLocalDateTime(requestDto);
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
