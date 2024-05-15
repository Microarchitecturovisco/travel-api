package org.microarchitecturovisco.hotelservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelsBySearchQueryRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;


public class JsonReader {

    public static GetHotelsBySearchQueryRequestDto readGetHotelsBySearchQueryRequestFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, GetHotelsBySearchQueryRequestDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reader failed");
        }
    }
}
