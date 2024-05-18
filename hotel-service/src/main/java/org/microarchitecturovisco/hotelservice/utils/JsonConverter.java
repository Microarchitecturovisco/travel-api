package org.microarchitecturovisco.hotelservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelDetailsRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelDetailsResponseDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelsBySearchQueryResponseDto;

public class JsonConverter {
    public static String convertGetHotelsBySearchQueryResponseDto (GetHotelsBySearchQueryResponseDto obj){
        return ConvertToJson(obj);
    }
    public static String convertGetHotelDetailsResponseDto (GetHotelDetailsResponseDto obj){
        return ConvertToJson(obj);
    }

    public static String ConvertToJson (Object obj){
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json;
        try {
            json = mapper.writeValueAsString(obj);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json failed to convert to JSON.");
        }
        return json;
    }

}
