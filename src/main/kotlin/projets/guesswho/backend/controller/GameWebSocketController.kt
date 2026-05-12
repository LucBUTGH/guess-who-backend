package projets.guesswho.backend.controller

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import projets.guesswho.backend.dto.ChatMessage
import projets.guesswho.backend.dto.GameEvent
import projets.guesswho.backend.dto.GuessMessage
import projets.guesswho.backend.service.GameService

@Controller
class GameWebSocketController(
    private val gameService: GameService,
    private val messagingTemplate: SimpMessagingTemplate
) {

    @MessageMapping("/game/{code}/guess")
    fun guess(
        @DestinationVariable code: String,
        @Payload message: GuessMessage,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val username = headerAccessor.user?.name ?: return
        gameService.guess(code, username, message.characterId)
    }

    @MessageMapping("/game/{code}/chat")
    fun chat(
        @DestinationVariable code: String,
        @Payload message: ChatMessage,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val username = headerAccessor.user?.name ?: return
        val event = GameEvent(type = "CHAT", sender = username, content = message.content)
        messagingTemplate.convertAndSend("/topic/game/$code", event)
    }
}
