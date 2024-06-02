package org.microarchitecturovisco.reservationservice.websockets;

import org.microarchitecturovisco.reservationservice.domain.dto.ReservationPreference;
import org.microarchitecturovisco.reservationservice.utils.json.JsonConverter;
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
    private final LinkedList<ReservationPreference> recentReservationRequests = new LinkedList<>();
    private final Map<String, Integer> hotelNameCounts = new HashMap<>();
    private final Map<String, Integer> roomNameCounts = new HashMap<>();
    private final Map<String, Integer> locationNameToCounts = new HashMap<>();
    private final Map<String, Integer> transportTypeCounts = new HashMap<>();

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

    public void updateReservationPreferences(ReservationPreference reservationPreference) {

        updateRecentReservationList(reservationPreference);
        updateCounts(reservationPreference);
        sendNewReservation(reservationPreference);
        sendTop3MostPopularTravelHotels();
        sendTop3MostPopularRoomTypes();
        sendTop3MostPopularLocationNamesTo();
        sendTop3MostPopularTransportTypes();
    }

    private void updateCounts(ReservationPreference reservationPreference) {

        String hotelName = reservationPreference.getHotelName();
        hotelNameCounts.put(hotelName, hotelNameCounts.getOrDefault(hotelName, 0) + 1);

        List<String> roomNames = reservationPreference.getRoomReservationsNames();
        for (String roomName : roomNames) {
            roomNameCounts.put(roomName, roomNameCounts.getOrDefault(roomName, 0) + 1);
        }

        String locationNameTo = reservationPreference.getLocationToNameRegionAndCountry();
        locationNameToCounts.put(locationNameTo, locationNameToCounts.getOrDefault(locationNameTo, 0) + 1);

        String transportType = reservationPreference.getTransportType();
        transportTypeCounts.put(transportType, transportTypeCounts.getOrDefault(transportType, 0) + 1);

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

    private void updateRecentReservationList(ReservationPreference reservationPreference) {
        recentReservationRequests.addLast(reservationPreference);
        int maxReservationRequestsCount = 30;
        if (recentReservationRequests.size() > maxReservationRequestsCount) {
            recentReservationRequests.removeLast();
        }
    }

    private void sendNewReservation(ReservationPreference reservationPreference) {
        String reservationPreferenceJson = JsonConverter.convert(reservationPreference);
        sendMessageToFrontend("SingleReservation: " + reservationPreferenceJson);
    }

    private void sendRecentReservationList() {
        for (ReservationPreference reservationPreference : recentReservationRequests) {
            String reservationPreferenceJson = JsonConverter.convert(reservationPreference);
            sendMessageToFrontend("SingleReservation: " + reservationPreferenceJson);
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
                .toList();

        List<String> topN = sortedCounts.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        sendMessageToFrontend(messageType + ": " + String.join(" # ", topN));
    }

    private List<String> extractHotelNames() {
        return recentReservationRequests.stream()
                .map(ReservationPreference::getHotelName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private List<String> extractRoomNames() {
        return recentReservationRequests.stream()
                .flatMap(request -> request.getRoomReservationsNames().stream()
                        .map(roomName -> request.getHotelName() + " - " + roomName))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private List<String> extractLocationNamesTo() {
        return recentReservationRequests.stream()
                .map(ReservationPreference::getLocationToNameRegionAndCountry)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private List<String> extractTransportTypes() {
        return recentReservationRequests.stream()
                .map(ReservationPreference::getTransportType)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
