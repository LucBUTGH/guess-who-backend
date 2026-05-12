package projets.guesswho.backend.dto

data class GameResponse(
    val code: String,
    val status: String,
    val player1: String,
    val player2: String?
)
