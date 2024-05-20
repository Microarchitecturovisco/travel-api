package org.microarchitecturovisco.transport.controllers.reservations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteTransportReservationRequest implements Serializable {
    private UUID id;
    private List<UUID> transportReservationsIds;
}