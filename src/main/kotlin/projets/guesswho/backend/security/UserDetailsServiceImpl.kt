package projets.guesswho.backend.security

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import projets.guesswho.backend.repository.PlayerRepository

@Service
class UserDetailsServiceImpl(
    private val playerRepository: PlayerRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val player = playerRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found")
        return User(player.username, player.password ?: "", emptyList())
    }
}
