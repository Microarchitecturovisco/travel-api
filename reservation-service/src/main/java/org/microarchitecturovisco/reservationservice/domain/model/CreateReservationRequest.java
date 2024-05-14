package org.microarchitecturovisco.reservationservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationRequest {
    private String id;
    private LocalDateTime hotelTimeFrom;
    private LocalDateTime hotelTimeTo;
    private int infantsQuantity;
    private int kidsQuantity;
    private int teensQuantity;
    private int adultsQuantity;
    private Map<String, String> roomTypes;
    private Integer idArrivalTransport;
    private Integer idReturnTransport;
    private Integer idUser;
}
