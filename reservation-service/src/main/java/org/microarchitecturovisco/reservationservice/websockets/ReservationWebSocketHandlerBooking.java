package org.microarchitecturovisco.reservationservice.websockets;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class ReservationWebSocketHandlerBooking extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private static final Logger logger = Logger.getLogger("ReservationWebSocketHandlerBooking");
    private final LinkedList<String> recentReservationRequests = new LinkedList<>();
    private final Map<String, Integer> hotelNameCounts = new HashMap<>();
    private final Map<String, Integer> roomNameCounts = new HashMap<>();
    private final Map<String, Integer> locationNameToCounts = new HashMap<>();
    private final Map<String, Integer> transportTypeCounts = new HashMap<>();
    private final int maxReservationRequestsCount = 30;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("Add session " + session.getId());
        sessions.add(session);
        sendRecentReservationList();
        sendTop3MostPopularTravelHotels();
        sendTop3MostPopularRoomTypes();
        sendTop3MostPopularLocationNamesTo();
        sendTop3MostPopularTransportTypes();
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

    public void updateReservationPreferences(String message) {
        updateRecentReservationList(message);
        updateCounts(message);
        sendNewReservation(message);
        sendTop3MostPopularTravelHotels();
        sendTop3MostPopularRoomTypes();
        sendTop3MostPopularLocationNamesTo();
        sendTop3MostPopularTransportTypes();
    }

    private void updateCounts(String message) {
        String[] parts = message.split(" \\| ");
        if (parts.length >= 5) {
            String hotelName = parts[0].split(": ")[2];
            hotelNameCounts.put(hotelName, hotelNameCounts.getOrDefault(hotelName, 0) + 1);

            String[] roomNames = parts[1].split(": ")[1].split(", ");
            for (String roomName : roomNames) {
                roomNameCounts.put(roomName, roomNameCounts.getOrDefault(roomName, 0) + 1);
            }

            String locationNameTo = parts[3].split(": ")[1];
            locationNameToCounts.put(locationNameTo, locationNameToCounts.getOrDefault(locationNameTo, 0) + 1);

            String transportType = parts[4].split(": ")[1];
            transportTypeCounts.put(transportType, transportTypeCounts.getOrDefault(transportType, 0) + 1);
        }
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

    private void updateRecentReservationList(String message) {
        recentReservationRequests.addLast(message);
        if (recentReservationRequests.size() > maxReservationRequestsCount) {
            recentReservationRequests.removeLast();
        }
    }

    private void sendNewReservation(String message) {
        sendMessageToFrontend("SingleReservation: " + message);
    }

    private void sendRecentReservationList() {
        for (String request : recentReservationRequests) {
            sendMessageToFrontend("SingleReservation: " + request);
        }
    }

    private void sendTop3MostPopularTravelHotels() {
        List<String> hotelNames = extractHotelNames();
        sendTopMostPopularData(hotelNames, "TopHotels", 3);
    }

    private void sendTop3MostPopularRoomTypes() {
        List<String> roomNames = extractRoomNames();
        sendTopMostPopularData(roomNames, "TopRoomTypes", 3);
    }

    private void sendTop3MostPopularLocationNamesTo() {
        List<String> locationNamesTo = extractLocationNamesTo();
        sendTopMostPopularData(locationNamesTo, "TopLocationNamesTo", 3);
    }

    private void sendTop3MostPopularTransportTypes() {
        List<String> transportTypes = extractTransportTypes();
        sendTopMostPopularData(transportTypes, "TopTransportTypes", 3);
    }

    private void sendTopMostPopularData(List<String> data, String messageType, int n) {
        Map<String, Long> counts = data.stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        List<Map.Entry<String, Long>> sortedCounts = counts.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(n)
                .collect(Collectors.toList());

        List<String> topN = sortedCounts.stream()
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        sendMessageToFrontend(messageType + ": " + String.join(" # ", topN));
    }

    private List<String> extractHotelNames() {
        return recentReservationRequests.stream()
                .map(request -> {
                    String[] parts = request.split(" \\| ");
                    if (parts.length >= 1) {
                        return parts[0].split(": ")[2];
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<String> extractRoomNames() {
        List<String> roomNames = new ArrayList<>();
        for (String request : recentReservationRequests) {
            String[] parts = request.split(" \\| ");
            if (parts.length >= 2) {
                String roomNamesString = parts[1].split(": ")[1];
                roomNames.addAll(Arrays.asList(roomNamesString.split(", ")));
            }
        }
        return roomNames;
    }

    private List<String> extractLocationNamesTo() {
        return recentReservationRequests.stream()
                .map(request -> {
                    String[] parts = request.split(" \\| ");
                    if (parts.length >= 4) {
                        return parts[3].split(": ")[1];
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<String> extractTransportTypes() {
        return recentReservationRequests.stream()
                .map(request -> {
                    String[] parts = request.split(" \\| ");
                    if (parts.length >= 5) {
                        return parts[4].split(": ")[1];
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
