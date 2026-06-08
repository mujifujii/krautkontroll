package kkbf.krautkontroll.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP-ueber-WebSocket-Konfiguration.
 *
 * <ul>
 *   <li>Clients verbinden sich mit {@code /ws} (SockJS-Fallback aktiv).</li>
 *   <li>Server pusht Positionen an das Topic {@code /topic/positions}.</li>
 *   <li>Spaetere Client-&gt;Server-Befehle (Broadcast, Panik) laufen ueber das Praefix {@code /app}.</li>
 * </ul>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                // Nur der Angular-Dev-Server darf andocken (Dev-Setup).
                .setAllowedOriginPatterns("http://localhost:4200")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // In-Memory-Broker reicht fuers Skeleton; spaeter ggf. externer Broker (RabbitMQ) fuer Fan-out-Skalierung.
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
