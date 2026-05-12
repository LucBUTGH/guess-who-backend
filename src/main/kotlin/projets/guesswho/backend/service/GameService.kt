package projets.guesswho.backend.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import projets.guesswho.backend.dto.CharacterDto
import projets.guesswho.backend.dto.GameEvent
import projets.guesswho.backend.dto.GameResponse
import projets.guesswho.backend.entity.Game
import projets.guesswho.backend.entity.GameStatus
import projets.guesswho.backend.repository.CharacterRepository
import projets.guesswho.backend.repository.GameRepository
import projets.guesswho.backend.repository.PlayerRepository
import java.util.UUID

@Service
class GameService(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val characterRepository: CharacterRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {

    fun createGame(username: String): GameResponse {
        val player = playerRepository.findByUsername(username)
            ?: throw IllegalArgumentException("Player not found")
        val code = UUID.randomUUID().toString().substring(0, 6).uppercase()
        val game = Game(code = code, player1 = player)
        gameRepository.save(game)
        return game.toResponse()
    }

    @Transactional
    fun joinGame(code: String, username: String): GameResponse {
        val game = gameRepository.findByCode(code)
            ?: throw IllegalArgumentException("Game not found")
        if (game.status != GameStatus.WAITING)
            throw IllegalStateException("Game is not available to join")
        val player = playerRepository.findByUsername(username)
            ?: throw IllegalArgumentException("Player not found")
        if (game.player1.username == username)
            throw IllegalStateException("You are already in this game")

        game.player2 = player
        game.status = GameStatus.IN_PROGRESS
        assignCharacters(game)
        gameRepository.save(game)

        val p1Event = GameEvent(type = "GAME_START", character = game.player1Character!!.toDto())
        val p2Event = GameEvent(type = "GAME_START", character = game.player2Character!!.toDto())
        messagingTemplate.convertAndSendToUser(game.player1.username, "/queue/game", p1Event)
        messagingTemplate.convertAndSendToUser(player.username, "/queue/game", p2Event)

        return game.toResponse()
    }

    fun getGame(code: String): GameResponse {
        val game = gameRepository.findByCode(code)
            ?: throw IllegalArgumentException("Game not found")
        return game.toResponse()
    }

    @Transactional
    fun guess(code: String, username: String, characterId: Long) {
        val game = gameRepository.findByCodeForUpdate(code)
            ?: throw IllegalArgumentException("Game not found")
        if (game.status != GameStatus.IN_PROGRESS)
            throw IllegalStateException("Game is not in progress")

        val isPlayer1 = game.player1.username == username
        val isPlayer2 = game.player2?.username == username
        if (!isPlayer1 && !isPlayer2)
            throw IllegalStateException("You are not a player in this game")

        val opponentCharacter = (if (isPlayer1) game.player2Character else game.player1Character)
            ?: throw IllegalStateException("Characters not assigned")

        val opponent = if (isPlayer1) game.player2!! else game.player1
        val correct = opponentCharacter.id == characterId
        val winner = if (correct) (if (isPlayer1) game.player1 else game.player2!!) else opponent
        val loser = if (correct) opponent else (if (isPlayer1) game.player1 else game.player2!!)

        game.winner = winner
        game.status = GameStatus.FINISHED
        gameRepository.save(game)

        val winnerIsPlayer1 = winner.username == game.player1.username
        val winnerOpponentChar = if (winnerIsPlayer1) game.player2Character else game.player1Character
        val loserOpponentChar = if (winnerIsPlayer1) game.player1Character else game.player2Character

        val winEvent = GameEvent(type = "GAME_RESULT", result = "WIN", opponentCharacter = winnerOpponentChar?.toDto())
        val loseEvent = GameEvent(type = "GAME_RESULT", result = "LOSE", opponentCharacter = loserOpponentChar?.toDto())
        messagingTemplate.convertAndSendToUser(winner.username, "/queue/game", winEvent)
        messagingTemplate.convertAndSendToUser(loser.username, "/queue/game", loseEvent)
    }

    @Transactional
    fun abortGame(username: String) {
        val player = playerRepository.findByUsername(username) ?: return
        val game = gameRepository.findInProgressByPlayer(player) ?: return
        if (game.status == GameStatus.FINISHED) return
        game.status = GameStatus.FINISHED
        gameRepository.save(game)
        messagingTemplate.convertAndSend("/topic/game/${game.code}", GameEvent(type = "GAME_ABORT"))
    }

    private fun assignCharacters(game: Game) {
        val characters = characterRepository.findAll().shuffled()
        game.player1Character = characters[0]
        game.player2Character = characters[1]
    }

    private fun Game.toResponse() = GameResponse(
        code = code,
        status = status.name,
        player1 = player1.username,
        player2 = player2?.username
    )

    private fun projets.guesswho.backend.entity.Character.toDto() =
        CharacterDto(id = id, name = name, imageUrl = imageUrl)
}
