package org.microarchitecturovisco.transport.model.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GetTransportsBySearchQueryRequestDto {
    private List<Integer> departureLocationIdsByPlane;
    private List<Integer> departureLocationIdsByBus;
    private List<Integer> arrivalLocationIds;

    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    private Integer adults;
    private Integer childrenUnderThree;
    private Integer childrenUnderTen;
    private Integer childrenUnderEighteen;
}
