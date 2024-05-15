package org.microarchitecturovisco.transport.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microarchitecturovisco.transport.model.domain.TransportType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTransportsBetweenLocationsRequestDto implements Serializable {
    UUID uuid;

    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    private TransportType transportType;

    private UUID departureLocationId;
    private UUID arrivalLocationId;

    private Integer adults;
    private Integer childrenUnderThree;
    private Integer childrenUnderTen;
    private Integer childrenUnderEighteen;
}
