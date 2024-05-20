package org.microarchitecturovisco.reservationservice.queues.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckTransportAvailabilityRequest implements Serializable {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    private TransportType transportType;

    private List<UUID> departureLocationIds;
    private List<UUID> arrivalLocationIds;

    private Integer adults;
    private Integer childrenUnderThree;
    private Integer childrenUnderTen;
    private Integer childrenUnderEighteen;
}


