package org.microarchitecturovisco.reservationservice.queues.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDeleteRequest implements Serializable {
    private UUID id;
}
