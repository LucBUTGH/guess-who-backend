package projets.guesswho.backend.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService {

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.expiration}")
    private var expiration: Long = 86400000

    fun generateToken(username: String): String {
        val key = Keys.hmacShaKeyFor(secret.toByteArray())
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(key)
            .compact()
    }

    fun extractUsername(token: String): String? {
        return try {
            val key = Keys.hmacShaKeyFor(secret.toByteArray())
            Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).payload.subject
        } catch (e: Exception) {
            null
        }
    }

    fun isTokenValid(token: String): Boolean {
        return try {
            val key = Keys.hmacShaKeyFor(secret.toByteArray())
            val expiry = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).payload.expiration
            expiry.after(Date())
        } catch (e: Exception) {
            false
        }
    }
}
