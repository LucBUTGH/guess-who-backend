package projets.guesswho.backend.dto

data class CharacterDto(
    val id: Long,
    val name: String,
    val imageUrl: String?
)

data class GameEvent(
    val type: String,
    val character: CharacterDto? = null,
    val result: String? = null,
    val opponentCharacter: CharacterDto? = null,
    val sender: String? = null,
    val content: String? = null
)

data class GuessMessage(val characterId: Long)

data class ChatMessage(val content: String)
