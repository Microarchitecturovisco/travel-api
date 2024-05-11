package org.microarchitecturovisco.hotelservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="room_reservations")
public class RoomReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    private LocalDateTime dateFrom;

    @NotNull
    private LocalDateTime dateTo;

    @ManyToOne()
    @JoinColumn(name="room_id")
    private Room room;

    public RoomReservation(LocalDateTime dateFrom, LocalDateTime dateTo, Room room) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.room = room;
    }
}
