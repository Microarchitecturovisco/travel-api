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

@Service
@RequiredArgsConstructor
public class ReservationService {
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
        System.out.println("transportIsAvailable: "+ hotelIsAvailable);
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



        return null; // Id rezerwacji
    }

}
