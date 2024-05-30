package org.microarchitecturovisco.reservationservice.websockets;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

@Component
public class ReservationWebSocketHandlerBooking extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private static final Logger logger = Logger.getLogger("ReservationWebSocketHandlerBooking");

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("Add session " + session.getId());
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Logger logger = Logger.getLogger("ReservationWebSocketHandlerBooking");
        logger.info("Session ended " + session.getId());
        sessions.remove(session);
        super.afterConnectionClosed(session, status);
    }

    public void sendMessageToUI(String message) {
        for (WebSocketSession session : sessions) {
            logger.info("Session ID: " + session.getId() + ", isOpen: " + session.isOpen());
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                    logger.info("Sending message: " + message + " to UI.");
                }
            } catch (IOException e) {
                logger.severe("Error sending message: " + e.getMessage());
            }
        }
    }

}