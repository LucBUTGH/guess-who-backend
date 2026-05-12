package projets.guesswho.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AuthRequest(
    @field:NotBlank
    @field:Size(max = 50)
    val username: String,

    @field:NotBlank
    @field:Size(max = 100)
    val password: String
)
