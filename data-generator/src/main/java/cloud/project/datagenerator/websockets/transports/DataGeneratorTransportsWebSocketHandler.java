package cloud.project.datagenerator.websockets.transports;

import cloud.project.datagenerator.rabbitmq.json.JsonConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

@Component
public class DataGeneratorTransportsWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private static final Logger logger = Logger.getLogger("DataGeneratorTransportsWebSocketHandler");
    private final LinkedList<TransportUpdate> recentTransportUpdates = new LinkedList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("Add session " + session.getId());
        sessions.add(session);
        sendAllTransportUpdates();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Session ended " + session.getId());
        sessions.remove(session);
        super.afterConnectionClosed(session, status);
    }

    public void updateTransportList(TransportUpdate transportUpdate) {
        updateTransportUpdatesList(transportUpdate);
        sendNewTransportUpdate(transportUpdate);
    }
    
    public void sendMessageToFrontend(String message) {
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
    }

    private void updateTransportUpdatesList(TransportUpdate transportUpdate) {
        recentTransportUpdates.addLast(transportUpdate);
        int maxTransportRequestsCount = 30;
        if (recentTransportUpdates.size() > maxTransportRequestsCount) {
            recentTransportUpdates.removeLast();
        }
    }

    private void sendNewTransportUpdate(TransportUpdate transportUpdate) {
        String transportUpdateJson = JsonConverter.convert(transportUpdate);
        sendMessageToFrontend("SingleTransport: " + transportUpdateJson);
    }

    private void sendAllTransportUpdates() {
        for (TransportUpdate transportUpdate : recentTransportUpdates) {
            String transportUpdateJson = JsonConverter.convert(transportUpdate);
            sendMessageToFrontend("SingleTransport: " + transportUpdateJson);
        }
    }
}
