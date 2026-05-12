package projets.guesswho.backend.dto

data class WsMessage(
    val sender: String,
    val content: String,
    val type: String = "CHAT"
)
