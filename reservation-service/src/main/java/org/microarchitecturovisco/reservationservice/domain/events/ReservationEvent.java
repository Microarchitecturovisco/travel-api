package org.microarchitecturovisco.reservationservice.domain.events;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import java.util.Date;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ReservationEvent {
    @Id
    public final String uuid = UUID.randomUUID().toString();
    public final Date timestamp = new Date();
}
