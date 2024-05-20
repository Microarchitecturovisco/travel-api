package org.microarchitecturovisco.hotelservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.microarchitecturovisco.hotelservice.controllers.reservations.CheckHotelAvailabilityRequest;
import org.microarchitecturovisco.hotelservice.controllers.reservations.CreateHotelReservationRequest;
import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelDetailsRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelsBySearchQueryRequestDto;


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
    public static GetHotelDetailsRequestDto readGetHotelDetailsRequestFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, GetHotelDetailsRequestDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reader failed");
        }
    }


    public static CreateRoomReservationCommand readCreateRoomReservationCommand(String json) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, CreateRoomReservationCommand.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reader failed");
        }
    }

    public static CheckHotelAvailabilityRequest readCheckHotelAvailabilityRequestCommand(String json) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, CheckHotelAvailabilityRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reader failed");
        }
    }
    public static CreateHotelReservationRequest readCreateHotelReservationRequestCommand(String json) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, CreateHotelReservationRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reader failed");
        }
    }
}
