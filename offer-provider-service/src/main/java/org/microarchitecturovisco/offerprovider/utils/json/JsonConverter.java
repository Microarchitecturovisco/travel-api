package org.microarchitecturovisco.offerprovider.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.microarchitecturovisco.offerprovider.domain.dto.requests.GetHotelsBySearchQueryRequestDto;
import org.microarchitecturovisco.offerprovider.domain.dto.requests.GetTransportsMessage;

public class JsonConverter {
    public static String convertGetTransportsMessage(GetTransportsMessage obj){
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

    public static String convert(Object obj) {
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
