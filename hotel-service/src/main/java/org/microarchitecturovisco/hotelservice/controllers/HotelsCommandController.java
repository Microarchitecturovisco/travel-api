package org.microarchitecturovisco.hotelservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.model.cqrs.commands.CreateRoomReservationCommand;
import org.microarchitecturovisco.hotelservice.services.HotelsCommandService;
import org.microarchitecturovisco.hotelservice.utils.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class HotelsCommandController {
    private final HotelsCommandService hotelsCommandService;

    @RabbitListener(queues = "#{createRoomReservationRequestQueue.name}")
    public void consumeCreateRoomReservationCommand(String commandDtoJson) {
        CreateRoomReservationCommand command = JsonReader.readCreateRoomReservationCommand(commandDtoJson);

        hotelsCommandService.createReservation(command);
    }
}
