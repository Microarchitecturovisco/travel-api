package org.microarchitecturovisco.offerprovider.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.GetHotelsBySearchQueryResponseDto;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.TransportsBasedOnSearchQueryResponse;

public class JsonReader {
    public static TransportsBasedOnSearchQueryResponse readTransportsBasedOnSearchQueryResponseFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, TransportsBasedOnSearchQueryResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reader failed");
        }
    }

    public static GetHotelsBySearchQueryResponseDto readHotelsBySearchQueryResponseDtoFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, GetHotelsBySearchQueryResponseDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reader failed");
        }
    }

}
