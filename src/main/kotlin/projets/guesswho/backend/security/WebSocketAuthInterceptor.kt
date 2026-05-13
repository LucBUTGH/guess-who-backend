package projets.guesswho.backend.security

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import projets.guesswho.backend.service.JwtService
import projets.guesswho.backend.repository.PlayerRepository
import org.springframework.security.core.userdetails.User

@Component
class WebSocketAuthInterceptor(
    private val jwtService: JwtService,
    private val playerRepository: PlayerRepository
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: return message

        if (accessor.command == StompCommand.CONNECT) {
            val authHeader = accessor.getFirstNativeHeader("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                val username = jwtService.extractUsername(token)
                if (username != null && jwtService.isTokenValid(token)) {
                    val player = playerRepository.findByUsername(username)
                    if (player != null) {
                        val userDetails = User(player.username, player.password ?: "", emptyList())
                        accessor.user = UsernamePasswordAuthenticationToken(userDetails, null, emptyList())
                        accessor.sessionAttributes?.put("username", player.username)
                    }
                }
            }
        } else if (accessor.user == null) {
            val username = accessor.sessionAttributes?.get("username") as? String
            if (username != null) {
                val userDetails = User(username, "", emptyList())
                accessor.user = UsernamePasswordAuthenticationToken(userDetails, null, emptyList())
            }
        }

        return message
    }
}
