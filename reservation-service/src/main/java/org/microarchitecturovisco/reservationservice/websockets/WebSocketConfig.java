package org.microarchitecturovisco.reservationservice.websockets;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ReservationWebSocketHandler webSocketHandler;
    private final ReservationWebSocketHandlerPreferences webSocketHandlerReservationPreferences;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/reservations/ws/offerBought").setAllowedOrigins("*");
        registry.addHandler(webSocketHandlerReservationPreferences, "/reservations/ws/reservationPreferences").setAllowedOrigins("*");
    }
}

