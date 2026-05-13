package projets.guesswho.backend.security

import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import projets.guesswho.backend.service.GameService
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Component
class WebSocketEventListener(
    private val gameService: GameService
) {
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val pendingAborts = ConcurrentHashMap<String, ScheduledFuture<*>>()

    @EventListener
    fun onConnect(event: SessionConnectEvent) {
        val accessor = StompHeaderAccessor.wrap(event.message)
        val username = accessor.sessionAttributes?.get("username") as? String ?: return
        pendingAborts.remove(username)?.cancel(false)
    }

    @EventListener
    fun onDisconnect(event: SessionDisconnectEvent) {
        val accessor = StompHeaderAccessor.wrap(event.message)
        val username = accessor.sessionAttributes?.get("username") as? String ?: return
        val future = scheduler.schedule(
            { gameService.abortGame(username) },
            15L, TimeUnit.SECONDS
        )
        pendingAborts[username] = future
    }
}
