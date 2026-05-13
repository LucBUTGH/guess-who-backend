package projets.guesswho.backend.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val webSocketAuthInterceptor: WebSocketAuthInterceptor,
    @Value("\${allowed.origins}") private val allowedOriginsRaw: String
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        val scheduler = ThreadPoolTaskScheduler().apply {
            poolSize = 1
            threadNamePrefix = "ws-heartbeat-"
            initialize()
        }
        config.enableSimpleBroker("/topic", "/queue")
            .setHeartbeatValue(longArrayOf(10000, 10000))
            .setTaskScheduler(scheduler)
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns(*allowedOriginsRaw.split(",").map { it.trim() }.toTypedArray())
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(webSocketAuthInterceptor)
    }
}
