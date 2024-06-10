package org.microarchitecturovisco.transport.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.cqrs.commands.CreateTransportReservationCommand;
import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.model.dto.data_generator.DataUpdateType;
import org.microarchitecturovisco.transport.model.dto.data_generator.TransportUpdateRequest;
import org.microarchitecturovisco.transport.services.TransportCommandService;
import org.microarchitecturovisco.transport.services.TransportsQueryService;
import org.microarchitecturovisco.transport.utils.json.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class TransportsCommandController {

    private final TransportCommandService transportCommandService;
    private final TransportsQueryService transportsQueryService;

    @RabbitListener(queues = "#{createTransportReservationRequestQueue.name}")
    public void consumeCreateTransportReservationCommand(String commandDtoJson) {
        CreateTransportReservationCommand command = JsonReader.readCreateTransportReservationCommand(commandDtoJson);

        transportCommandService.createReservation(command);
    }

    @RabbitListener(queues = "#{handleDataGeneratorCreateQueue}")
    public void consumeDataGeneratorMessage(String requestJson) {
        Logger logger = Logger.getLogger("TransportController");
        logger.info("Got Create/Update transport request from Data Generator: " + requestJson);

        TransportUpdateRequest request = JsonReader.readDtoFromJson(requestJson, TransportUpdateRequest.class);

        // create transport
        if (request.getUpdateType() == DataUpdateType.CREATE) {
            logger.info("Created transport: " + request);

            transportCommandService.createTransport(request.getId(), request.getCourseId(), request.getDepartureDate(),
                    request.getCapacity(), request.getPricePerAdult());
            return;
        }

        Transport transport = transportsQueryService.getTransportById(request.getId());
        if (transport == null)
        {
            return;
        }
        // update transport
        if (request.getUpdateType() == DataUpdateType.UPDATE) {
            logger.info("Updated transport: " + request);

            transportCommandService.updateTransport(request.getId(), request.getCapacity(), request.getPricePerAdult());
        }
    }
}
