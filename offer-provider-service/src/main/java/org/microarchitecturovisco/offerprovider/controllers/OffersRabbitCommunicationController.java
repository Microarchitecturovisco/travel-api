package org.microarchitecturovisco.offerprovider.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.requests.RoomUpdateRequest;
import org.microarchitecturovisco.offerprovider.domain.requests.TransportUpdateRequest;
import org.microarchitecturovisco.offerprovider.domain.responses.GetOfferDetailsResponseDto;
import org.microarchitecturovisco.offerprovider.utils.json.JsonConverter;
import org.microarchitecturovisco.offerprovider.utils.json.JsonReader;
import org.microarchitecturovisco.offerprovider.websockets.OfferDetailsWebSocketHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class OffersRabbitCommunicationController {

    private final OfferDetailsWebSocketHandler offerDetailsWebSocketHandler;

    private final OffersController offersController;

    @RabbitListener(queues = "#{hotelDataGeneratorQueue}")
    public void consumeHotelDataGeneratorMessage(String requestJson) {
        Logger logger = Logger.getLogger("consumeHotelDataGeneratorMessage");

        RoomUpdateRequest request = JsonReader.readJson(requestJson, RoomUpdateRequest.class);

        logger.info("Hotel " + request.getHotelId() + " " + request.getUpdateType().getValue() + " update");

        offerDetailsWebSocketHandler.sendOfferDetailsToSubscribedByHotelId(String.valueOf(request.getHotelId()));
    }

    @RabbitListener(queues = "#{transportDataGeneratorQueue}")
    public void consumeTransportDataGeneratorMessage(String requestJson) {
        Logger logger = Logger.getLogger("consumeTransportDataGeneratorMessage");

        TransportUpdateRequest request = JsonReader.readJson(requestJson, TransportUpdateRequest.class);

        logger.info("Transport " + request.getCourseId() + " " + request.getUpdateType() + " update");

        offerDetailsWebSocketHandler.sendOfferDetailsToSubscribedByLocationIds(String.valueOf(request.getCourseLocationToId()), String.valueOf(request.getCourseLocationFromId()));
    }
}
