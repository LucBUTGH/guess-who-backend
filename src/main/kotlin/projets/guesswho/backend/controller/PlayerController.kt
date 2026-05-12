package projets.guesswho.backend.controller

import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import projets.guesswho.backend.repository.GameRepository
import projets.guesswho.backend.repository.PlayerRepository

@RestController
@RequestMapping("/api")
class PlayerController(
    private val playerRepository: PlayerRepository,
    private val gameRepository: GameRepository
) {

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal user: UserDetails): ResponseEntity<Map<String, Any>> {
        val player = playerRepository.findByUsername(user.username)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(mapOf(
            "username" to player.username,
            "wins" to playerRepository.countWins(player),
            "losses" to playerRepository.countLosses(player)
        ))
    }

    @GetMapping("/me/games")
    fun myGames(@AuthenticationPrincipal user: UserDetails): ResponseEntity<List<Map<String, Any>>> {
        val player = playerRepository.findByUsername(user.username)
            ?: return ResponseEntity.notFound().build()
        val games = gameRepository.findFinishedGamesByPlayer(player)
        return ResponseEntity.ok(games.map { game ->
            val isPlayer1 = game.player1.username == player.username
            val opponent = if (isPlayer1) game.player2 else game.player1
            val myChar = if (isPlayer1) game.player1Character else game.player2Character
            val oppChar = if (isPlayer1) game.player2Character else game.player1Character
            mapOf(
                "result" to if (game.winner?.username == player.username) "WIN" else "LOSE",
                "opponent" to (opponent?.username ?: "Inconnu"),
                "myCharacter" to mapOf("name" to myChar?.name, "imageUrl" to myChar?.imageUrl),
                "opponentCharacter" to mapOf("name" to oppChar?.name, "imageUrl" to oppChar?.imageUrl)
            )
        })
    }

    @GetMapping("/leaderboard")
    fun leaderboard(): ResponseEntity<List<Map<String, Any>>> {
        val players = playerRepository.findTop10ByWins(PageRequest.of(0, 10))
        return ResponseEntity.ok(players.map {
            mapOf(
                "username" to it.username,
                "wins" to playerRepository.countWins(it),
                "losses" to playerRepository.countLosses(it)
            )
        })
    }
}
