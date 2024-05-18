package org.microarchitecturovisco.reservationservice.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonReader {
    public static <T> T readDtoFromJson(String json, Class<T> dtoClass) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, dtoClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reader failed");
        }
    }
}
