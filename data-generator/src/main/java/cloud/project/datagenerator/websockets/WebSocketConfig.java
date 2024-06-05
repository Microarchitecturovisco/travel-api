package cloud.project.datagenerator.websockets;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final DataGeneratorHotelsWebSocketHandler webSocketHandlerHotels;
    private final DataGeneratorTransportsWebSocketHandler webSocketHandlerTransports;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandlerHotels, "/data-generator/ws/hotel").setAllowedOrigins("*");
        registry.addHandler(webSocketHandlerTransports, "/data-generator/ws/transport").setAllowedOrigins("*");
    }
}

