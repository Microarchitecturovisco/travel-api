package org.microarchitecturovisco.transport.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportReservationCommand;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBetweenLocationsRequestDto;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;

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

    public static GetTransportsBySearchQueryRequestDto readGetTransportsBySearchQueryRequestFromJson(String json) {
        return readDtoFromJson(json, GetTransportsBySearchQueryRequestDto.class);
    }

    public static CreateTransportReservationCommand readCreateTransportReservationCommand(String json) {
        return readDtoFromJson(json, CreateTransportReservationCommand.class);
    }

    public static GetTransportsBetweenLocationsRequestDto readGetTransportsBetweenLocationsRequestDtoFromJson(String json) {
        return readDtoFromJson(json, GetTransportsBetweenLocationsRequestDto.class);
    }
}
