package org.microarchitecturovisco.offerprovider.websockets;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final OfferDetailsWebSocketHandler offerDetailsWebSocketHandler;
    private final OfferPriceWebSocketHandler offerPriceWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(offerDetailsWebSocketHandler, "/offers/ws/offerDetails").setAllowedOrigins("*");
        registry.addHandler(offerPriceWebSocketHandler, "/offers/ws/offerPrice").setAllowedOrigins("*");
    }
}

