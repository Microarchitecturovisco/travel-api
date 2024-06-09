package cloud.project.datagenerator.websockets.hotels;

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
public class DataGeneratorHotelsWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private static final Logger logger = Logger.getLogger("DataGeneratorHotelsWebSocketHandler");
    private final LinkedList<HotelUpdate> recentHotelUpdates = new LinkedList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("Add session " + session.getId());
        sessions.add(session);
        sendAllHotelUpdates();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Session ended " + session.getId());
        sessions.remove(session);
        super.afterConnectionClosed(session, status);
    }

    public void updateHotelList(HotelUpdate hotelUpdate) {
        updateHotelUpdatesList(hotelUpdate);
        sendNewHotelUpdate(hotelUpdate);
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

    private void updateHotelUpdatesList(HotelUpdate hotelUpdate) {
        recentHotelUpdates.addLast(hotelUpdate);
        int maxHotelRequestsCount = 30;
        if (recentHotelUpdates.size() > maxHotelRequestsCount) {
            recentHotelUpdates.removeFirst();
        }
    }

    private void sendNewHotelUpdate(HotelUpdate hotelUpdate) {
        String hotelUpdateJson = JsonConverter.convert(hotelUpdate);
        sendMessageToFrontend("SingleHotel: " + hotelUpdateJson);
    }

    private void sendAllHotelUpdates() {
        for (HotelUpdate hotelUpdate : recentHotelUpdates) {
            String hotelUpdateJson = JsonConverter.convert(hotelUpdate);
            sendMessageToFrontend("SingleHotel: " + hotelUpdateJson);
        }
    }
}
