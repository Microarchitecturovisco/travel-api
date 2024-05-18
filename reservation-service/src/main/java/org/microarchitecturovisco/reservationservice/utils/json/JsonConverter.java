package org.microarchitecturovisco.reservationservice.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonConverter {
    public static String convertToJsonWithLocalDateTime(Object obj) {
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
