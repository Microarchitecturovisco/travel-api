package org.microarchitecturovisco.transport.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTransportsBySearchQueryRequestDto implements Serializable {
    private String uuid;

    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    private List<Integer> departureLocationIdsByPlane;
    private List<Integer> departureLocationIdsByBus;
    private List<Integer> arrivalLocationIds;

    private Integer adults;
    private Integer childrenUnderThree;
    private Integer childrenUnderTen;
    private Integer childrenUnderEighteen;
}
