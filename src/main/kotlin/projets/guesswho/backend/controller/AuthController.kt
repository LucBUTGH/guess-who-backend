package projets.guesswho.backend.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import projets.guesswho.backend.dto.AuthRequest
import projets.guesswho.backend.dto.AuthResponse
import projets.guesswho.backend.service.AuthService
import projets.guesswho.backend.service.LoginRateLimiter

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val rateLimiter: LoginRateLimiter
) {

    @PostMapping("/register")
    fun register(@RequestBody @Valid request: AuthRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.register(request))
    }

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid request: AuthRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<AuthResponse> {
        if (!rateLimiter.isAllowed(httpRequest.remoteAddr)) {
            return ResponseEntity.status(429).build()
        }
        return ResponseEntity.ok(authService.login(request))
    }
}
