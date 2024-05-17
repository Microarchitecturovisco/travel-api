package org.microarchitecturovisco.offerprovider.domain.dto.requests;

import lombok.Builder;
import lombok.Data;
import org.microarchitecturovisco.offerprovider.domain.dto.TransportType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetTransportsMessage implements Serializable {
    private String uuid;

    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    private TransportType transportType;

//    private List<UUID> departureLocationIdsByPlane;
//    private List<UUID> departureLocationIdsByBus;
    private List<UUID> departureLocationIds;
    private List<UUID> arrivalLocationIds;

    private Integer adults;
    private Integer childrenUnderThree;
    private Integer childrenUnderTen;
    private Integer childrenUnderEighteen;

}
