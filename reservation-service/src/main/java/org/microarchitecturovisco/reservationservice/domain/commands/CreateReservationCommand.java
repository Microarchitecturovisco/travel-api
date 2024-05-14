package org.microarchitecturovisco.reservationservice.domain.commands;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class CreateReservationCommand {
    @Id
    private String id;
    private LocalDateTime hotelTimeFrom;
    private LocalDateTime hotelTimeTo;
    private int infantsQuantity;
    private int kidsQuantity;
    private int teensQuantity;
    private int adultsQuantity;
    private float price;
    private boolean paid;
    private int hotelId;
    @ElementCollection
    private List<String> roomReservationsIds;
    @ElementCollection
    private List<String> transportReservationsIds;
    private int userId;
}
