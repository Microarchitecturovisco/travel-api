package org.microarchitecturovisco.reservationservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPreference {
    private String hotelName;
    private List<String> roomReservationsNames;
    private String locationFromNameRegionAndCountry;
    private String locationToNameRegionAndCountry;
    private String transportType;
    private String reservationTime;
}