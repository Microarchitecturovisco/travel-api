package org.microarchitecturovisco.offerprovider.websockets;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.offerprovider.controllers.OffersController;
import org.microarchitecturovisco.offerprovider.domain.requests.GetOfferDetailsRequestDto;
import org.microarchitecturovisco.offerprovider.domain.responses.GetOfferDetailsResponseDto;
import org.microarchitecturovisco.offerprovider.utils.json.JsonConverter;
import org.microarchitecturovisco.offerprovider.utils.json.JsonReader;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class OfferDetailsWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final OffersController offersController;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        URI uri = session.getUri();
        Logger logger = Logger.getLogger("OfferDetailsWebSocketHandler");

        if (uri != null) {
            Map<String, List<String>> queryParams = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
            List<String> offerIds = queryParams.get("idHotel");
            if (offerIds != null) {
                session.getAttributes().put("idHotel", offerIds.getFirst());
                logger.info("Add session " + session.getId() + " with idHotel attr " + offerIds.getFirst());
            }
        }
        else {
            logger.info("Add session " + session.getId());
        }

        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

        GetOfferDetailsRequestDto requestDto = JsonReader.readJson(message.getPayload(), GetOfferDetailsRequestDto.class);

        GetOfferDetailsResponseDto responseDto = offersController.getOfferDetails(requestDto);

        // add hotel location id for transport course updates
        session.getAttributes().put("idHotelLocation", responseDto.getDeparture().getFirst().getTransportCourse().getArrivalAtLocation().getIdLocation().toString());
        session.getAttributes().put("searchParams", requestDto);

        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(JsonConverter.convert(responseDto)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Logger logger = Logger.getLogger("OfferDetailsWebSocketHandler");
        logger.info("Session ended " + session.getId());
        sessions.remove(session);
        super.afterConnectionClosed(session, status);
    }

    public void sendOfferDetailsToSubscribedByHotelId(String idHotel) {
        Logger logger = Logger.getLogger("ReservationWebSocketHandler");
        logger.info("Sending message to clients subscribed to hotel " + idHotel);

        for (WebSocketSession session : sessions.stream().filter(sess -> sess.getAttributes().get("idHotel").equals(idHotel)).toList()) {
            try {
                if (session.isOpen()) {
                    GetOfferDetailsResponseDto responseDto = offersController.getOfferDetails((GetOfferDetailsRequestDto) session.getAttributes().get("searchParams"));

                    responseDto.setDataGeneratorUpdate(true);

                    session.sendMessage(new TextMessage(JsonConverter.convert(responseDto)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendOfferDetailsToSubscribedByLocationIds(String idFromLocation, String idToLocation) {
        Logger logger = Logger.getLogger("ReservationWebSocketHandler");
        logger.info("Sending message to clients subscribed to one of those locations: " + idFromLocation + " " + idToLocation);

        for (WebSocketSession session : sessions.stream().filter(sess ->
                sess.getAttributes().get("idHotelLocation") != null &&
                        (sess.getAttributes().get("idHotelLocation").equals(idFromLocation) || sess.getAttributes().get("idHotelLocation").equals(idToLocation))).toList()) {
            try {
                if (session.isOpen()) {
                    logger.info("sending message to session with hotel id " + session.getAttributes().get("idHotel"));

                    GetOfferDetailsResponseDto responseDto = offersController.getOfferDetails((GetOfferDetailsRequestDto) session.getAttributes().get("searchParams"));

                    responseDto.setDataGeneratorUpdate(true);

                    session.sendMessage(new TextMessage(JsonConverter.convert(responseDto)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
