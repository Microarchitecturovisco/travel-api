package org.microarchitecturovisco.transport.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportReservationCommand;
import org.microarchitecturovisco.transport.services.TransportCommandService;
import org.microarchitecturovisco.transport.utils.json.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransportsCommandController {

    private final TransportCommandService transportCommandService;

    @RabbitListener(queues = "#{createTransportReservationRequestQueue.name}")
    public void consumeCreateTransportReservationCommand(String commandDtoJson) {
        CreateTransportReservationCommand command = JsonReader.readCreateTransportReservationCommand(commandDtoJson);

        transportCommandService.createReservation(command);
    }
}
