package org.microarchitecturovisco.reservationservice.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.reservationservice.domain.commands.CreateReservationCommand;
import org.microarchitecturovisco.reservationservice.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private ReservationRepository reservationRepository;
    private ReservationAggregate reservationAggregate;

    public void createReservation(LocalDateTime hotelTimeFrom, LocalDateTime hotelTimeTo,
                                  int infantsQuantity, int kidsQuantity, int teensQuantity, int adultsQuantity,
                                  float price, boolean paid, int hotelId, List<Integer> roomReservationsIds,
                                  List<Integer> transportReservationsIds, int userId) {
        CreateReservationCommand command = CreateReservationCommand.builder()
                .id(UUID.randomUUID().toString())
                .hotelTimeFrom(hotelTimeFrom)
                .hotelTimeTo(hotelTimeTo)
                .infantsQuantity(infantsQuantity)
                .kidsQuantity(kidsQuantity)
                .teensQuantity(teensQuantity)
                .adultsQuantity(adultsQuantity)
                .price(price)
                .paid(paid)
                .hotelId(hotelId)
                .roomReservationsIds(roomReservationsIds)
                .transportReservationsIds(transportReservationsIds)
                .userId(userId)
                .build();

    }

}
