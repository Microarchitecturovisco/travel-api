package org.microarchitecturovisco.transport.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;

public class JsonConverter {
    public static String convertGetTransportsBySearchQueryResponseDto (GetTransportsBySearchQueryResponseDto obj){
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
