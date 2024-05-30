package org.microarchitecturovisco.reservationservice.websockets;

import com.google.common.collect.Lists;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

@Component
public class ReservationWebSocketHandlerBooking extends TextWebSocketHandler {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ReservationWebSocketHandlerBooking.class);
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private static final Logger logger = Logger.getLogger("ReservationWebSocketHandlerBooking");
    private final List<String> recentReservationRequests = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("Add session " + session.getId());
        sessions.add(session);
        sendRecentReservationList(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Session ended " + session.getId());
        sessions.remove(session);
        super.afterConnectionClosed(session, status);
    }

    public void sendMessageToUI(String message) {
        updateRecentReservationList(message);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                    logger.info("Sending message: " + message + " to UI.");
                }
            } catch (IOException e) {
                logger.severe("Error sending message: " + e.getMessage());
            }
        }

        for(String res: recentReservationRequests){
            logger.info("currently storing: " + res);
        }
    }

    private void updateRecentReservationList(String message) {
        recentReservationRequests.add(0, message);
        if (recentReservationRequests.size() > 3) {
            recentReservationRequests.remove(3);
        }
    }

    private void sendRecentReservationList(WebSocketSession session) {
        try {
            for (String request : Lists.reverse(recentReservationRequests)) {
                session.sendMessage(new TextMessage(request));
                logger.info("Sending recent reservation list to session " + session.getId());
            }
        } catch (IOException e) {
            logger.severe("Error sending recent reservation list: " + e.getMessage());
        }
    }
}
