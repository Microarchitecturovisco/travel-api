package org.microarchitecturovisco.reservationservice.websockets;

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
public class ReservationWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        URI uri = session.getUri();
        Logger logger = Logger.getLogger("ReservationWebSocketHandler");

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

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Logger logger = Logger.getLogger("ReservationWebSocketHandler");
        logger.info("Session ended " + session.getId());
        sessions.remove(session);
        super.afterConnectionClosed(session, status);
    }

    public void sendMessageToAll(String message) {
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessageToSubscribedByIdHotel(String message, String idHotel) {
        Logger logger = Logger.getLogger("ReservationWebSocketHandler");
        logger.info("Sending message to clients subscribed to " + idHotel);
        for (WebSocketSession session : sessions.stream().filter(sess -> sess.getAttributes().get("idHotel").equals(idHotel)).toList()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
