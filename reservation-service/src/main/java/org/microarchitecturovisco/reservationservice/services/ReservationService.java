package org.microarchitecturovisco.reservationservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.commands.CreateReservationCommand;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.domain.exceptions.ReservationFailException;
import org.microarchitecturovisco.reservationservice.queues.config.QueuesReservationConfig;
import org.microarchitecturovisco.reservationservice.queues.hotels.ReservationRequest;
import org.microarchitecturovisco.reservationservice.repositories.ReservationRepository;
import org.microarchitecturovisco.reservationservice.services.saga.BookHotelsSaga;
import org.microarchitecturovisco.reservationservice.services.saga.BookTransportsSaga;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final BookHotelsSaga bookHotelsSaga;
    private final BookTransportsSaga bookTransportsSaga;

    private final ReservationRepository reservationRepository;
    private final ReservationAggregate reservationAggregate;
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

    public UUID bookOrchestration(ReservationRequest reservationRequest) throws ReservationFailException {

        checkHotelAvailability(reservationRequest);

        checkTransportAvailability(reservationRequest);

        UUID reservationId = UUID.randomUUID();
        reservationRequest.setId(reservationId);

        createReservationFromRequest(reservationRequest);


        // todo: reserve hotel
        //  Wysyłany jest event zarezerwowania hotelu do kolejki hotels.events.createHotelReservation
        bookHotelsSaga.createHotelReservation(reservationRequest);

        // todo: reserve transport
        //  Wysyłany jest event zarezerwowania transportu do kolejki transports.events.createTransportReservation
        

        // todo: Rozpoczyna się odliczanie do przedawnienia się rezerwacji
        //  (co skutkuje cofnięciem poprzednich operacji);
        //  do aplikacji klienckiej zwracany jest status 2xx oraz idReservation tego zamówienia
        //  dodać pole Timestamp stworzenia rezerwacji do klasy Reservation



        return null; // reservationId
    }

    private void checkHotelAvailability(ReservationRequest reservationRequest) throws ReservationFailException {
        boolean hotelIsAvailable = bookHotelsSaga.checkIfHotelIsAvailable(reservationRequest);
//        boolean hotelIsAvailable = true; // debug only
        System.out.println("hotelIsAvailable: "+ hotelIsAvailable);
        if(!hotelIsAvailable) { throw new ReservationFailException(); }
    }

    private void checkTransportAvailability(ReservationRequest reservationRequest) throws ReservationFailException {
        boolean transportIsAvailable = bookTransportsSaga.checkIfTransportIsAvailable(reservationRequest);
//        boolean transportIsAvailable = true; // debug only
        System.out.println("transportIsAvailable: " + transportIsAvailable);
        if(!transportIsAvailable) { throw new ReservationFailException(); }
    }

    public void createReservationFromRequest(ReservationRequest reservationRequest) {
        rabbitTemplate.convertAndSend(QueuesReservationConfig.EXCHANGE_RESERVATION, "", reservationRequest);
        System.out.println("reservationCreated: " + reservationRequest.getId());
    }

}
