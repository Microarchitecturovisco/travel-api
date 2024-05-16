package org.microarchitecturovisco.transport.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTransportsBySearchQueryRequestDto implements Serializable {
    private UUID uuid;

    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    private List<UUID> departureLocationIdsByPlane;
    private List<UUID> departureLocationIdsByBus;
    private List<UUID> arrivalLocationIds;

    private Integer adults;
    private Integer childrenUnderThree;
    private Integer childrenUnderTen;
    private Integer childrenUnderEighteen;
}
