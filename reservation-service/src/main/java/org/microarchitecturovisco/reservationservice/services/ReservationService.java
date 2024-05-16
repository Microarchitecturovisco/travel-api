package org.microarchitecturovisco.reservationservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.commands.CreateReservationCommand;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationFailException;
import org.microarchitecturovisco.reservationservice.queues.hotels.ReservationRequest;
import org.microarchitecturovisco.reservationservice.repositories.ReservationRepository;
import org.microarchitecturovisco.reservationservice.services.saga.BookHotelsSaga;
import org.microarchitecturovisco.reservationservice.services.saga.BookTransportsSaga;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
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
    public Reservation createReservation(LocalDateTime hotelTimeFrom, LocalDateTime hotelTimeTo,
                                         int infantsQuantity, int kidsQuantity, int teensQuantity, int adultsQuantity,
                                         float price, int hotelId, List<String> roomReservationsIds,
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

}
