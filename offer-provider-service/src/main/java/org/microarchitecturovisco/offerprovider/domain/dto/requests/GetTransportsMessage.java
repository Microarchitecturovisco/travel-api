package org.microarchitecturovisco.offerprovider.domain.dto.requests;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GetTransportsMessage implements Serializable {
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
