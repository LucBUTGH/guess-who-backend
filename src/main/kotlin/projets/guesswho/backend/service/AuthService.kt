package projets.guesswho.backend.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import projets.guesswho.backend.dto.AuthRequest
import projets.guesswho.backend.dto.AuthResponse
import projets.guesswho.backend.entity.Player
import projets.guesswho.backend.repository.PlayerRepository

@Service
class AuthService(
    private val playerRepository: PlayerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    fun register(request: AuthRequest): AuthResponse {
        if (playerRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username already taken")
        }
        val player = Player(
            username = request.username,
            password = passwordEncoder.encode(request.password)
        )
        playerRepository.save(player)
        return AuthResponse(jwtService.generateToken(player.username))
    }

    fun login(request: AuthRequest): AuthResponse {
        val player = playerRepository.findByUsername(request.username)
            ?: throw IllegalArgumentException("Invalid credentials")
        if (!passwordEncoder.matches(request.password, player.password ?: "")) {
            throw IllegalArgumentException("Invalid credentials")
        }
        return AuthResponse(jwtService.generateToken(player.username))
    }
}
