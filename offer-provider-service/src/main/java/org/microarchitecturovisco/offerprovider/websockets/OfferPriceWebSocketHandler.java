package org.microarchitecturovisco.offerprovider.websockets;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.offerprovider.controllers.OffersController;
import org.microarchitecturovisco.offerprovider.domain.requests.GetOfferPriceRequestDto;
import org.microarchitecturovisco.offerprovider.utils.json.JsonReader;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class OfferPriceWebSocketHandler extends TextWebSocketHandler {
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final OffersController offersController;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Logger logger = Logger.getLogger("OfferPriceWebSocketHandler");
        logger.info("Add session " + session.getId());
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

        GetOfferPriceRequestDto requestDto = JsonReader.readJson(message.getPayload(), GetOfferPriceRequestDto.class);

        Float offerPrice = offersController.getOfferPrice(requestDto);

        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(offerPrice.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Logger logger = Logger.getLogger("OfferPriceWebSocketHandler");
        logger.info("Session ended " + session.getId());
        sessions.remove(session);
        super.afterConnectionClosed(session, status);
    }
}
