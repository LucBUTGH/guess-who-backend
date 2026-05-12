package projets.guesswho.backend.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import projets.guesswho.backend.dto.GameResponse
import projets.guesswho.backend.service.GameService

@RestController
@RequestMapping("/api/game")
class GameController(private val gameService: GameService) {

    @PostMapping("/create")
    fun create(@AuthenticationPrincipal user: UserDetails): ResponseEntity<GameResponse> {
        return ResponseEntity.ok(gameService.createGame(user.username))
    }

    @PostMapping("/join/{code}")
    fun join(
        @PathVariable code: String,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<GameResponse> {
        return ResponseEntity.ok(gameService.joinGame(code, user.username))
    }

    @GetMapping("/{code}")
    fun get(@PathVariable code: String): ResponseEntity<GameResponse> {
        return ResponseEntity.ok(gameService.getGame(code))
    }
}
