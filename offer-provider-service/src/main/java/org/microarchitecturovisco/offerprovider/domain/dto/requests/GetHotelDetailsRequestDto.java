package org.microarchitecturovisco.offerprovider.domain.dto.requests;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GetHotelDetailsRequestDto implements Serializable {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    private UUID hotelId;

    private Integer adults;
    private Integer childrenUnderThree;
    private Integer childrenUnderTen;
    private Integer childrenUnderEighteen;
}
