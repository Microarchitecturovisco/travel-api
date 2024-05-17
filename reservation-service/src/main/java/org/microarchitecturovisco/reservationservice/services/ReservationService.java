package org.microarchitecturovisco.reservationservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.commands.CreateReservationCommand;
import org.microarchitecturovisco.reservationservice.domain.entity.Reservation;
import org.microarchitecturovisco.reservationservice.domain.events.ReservationEvent;
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

        return reservationRepository.findById(reservationId).orElseThrow(RuntimeException::new);
    }


    public UUID bookOrchestration(ReservationRequest reservationRequest) throws ReservationFailException {

//        boolean hotelIsAvailable = bookHotelsSaga.checkIfHotelIsAvailable(reservationRequest);
        boolean hotelIsAvailable = true;
        System.out.println("hotelIsAvailable: " + hotelIsAvailable);
        if(!hotelIsAvailable) { throw new ReservationFailException(); }

//        boolean transportIsAvailable = bookTransportsSaga.checkIfTransportIsAvailable(reservationRequest);
        boolean transportIsAvailable = true;
        System.out.println("transportIsAvailable: " + transportIsAvailable);
        if(!transportIsAvailable) { throw new ReservationFailException(); }


        //todo: Create Reservation here --> use createReservation()
        // Tworzony jest obiekt rezerwacji
        // Orkiestrator wysyła event stworzenia obiektu do kolejki reservations.events.createReservation
        // Reservations tworzy instancje
        Reservation reservation = createReservationFromRequest(reservationRequest);
        System.out.println("MAIN reservationCreated: " + reservation);
        if(reservation==null) { throw new ReservationFailException(); }




        // todo: reserve hotel
        //  Wysyłany jest event zarezerwowania hotelu do kolejki hotels.events.createHotelReservation



        // todo: reserve transport
        //  Wysyłany jest event zarezerwowania transportu do kolejki transports.events.createTransportReservation


        // todo: Rozpoczyna się odliczanie do przedawnienia się rezerwacji
        //  (co skutkuje cofnięciem poprzednich operacji);
        //  do aplikacji klienckiej zwracany jest status 2xx oraz idReservation tego zamówienia
        //  dodać pole Timestamp stworzenia rezerwacji do klasy Reservation



        return null; // Id rezerwacji
    }


    public Reservation createReservationFromRequest(ReservationRequest reservationRequest){
        UUID reservationId = UUID.randomUUID();

        reservationRequest.setReservationId(reservationId);

        Reservation reservation = createReservation(
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

        rabbitTemplate.convertAndSend(
                QueuesReservationConfig.EXCHANGE_RESERVATION,
                QueuesReservationConfig.ROUTING_KEY_RESERVATION_CREATE_REQ,
                reservationRequest
        );

        return reservation;
    }
}
