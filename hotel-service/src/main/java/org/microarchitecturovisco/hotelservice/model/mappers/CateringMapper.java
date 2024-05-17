package org.microarchitecturovisco.hotelservice.model.mappers;

import org.microarchitecturovisco.hotelservice.model.domain.CateringOption;
import org.microarchitecturovisco.hotelservice.model.domain.Hotel;
import org.microarchitecturovisco.hotelservice.model.domain.Location;
import org.microarchitecturovisco.hotelservice.model.dto.CateringOptionDto;
import org.microarchitecturovisco.hotelservice.model.dto.HotelResponseDto;
import org.microarchitecturovisco.hotelservice.model.dto.LocationDto;

import java.util.ArrayList;
import java.util.List;

public class CateringMapper {

    public static CateringOptionDto map(CateringOption cateringOption) {
        return CateringOptionDto.builder()
                .price(cateringOption.getPrice())
                .type(cateringOption.getType())
                .cateringId(cateringOption.getId())
                .rating(cateringOption.getRating())
                .hotelId(cateringOption.getHotel().getId())
                .build();
    }

    public static List<CateringOptionDto> mapList(List<CateringOption> cateringOptions) {
        List<CateringOptionDto> cateringOptionDtos = new ArrayList<>();
        for (CateringOption cateringOption : cateringOptions) {
            cateringOptionDtos.add(map(cateringOption));
        }
        return cateringOptionDtos;
    }
}
