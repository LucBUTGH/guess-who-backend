package projets.guesswho.backend.repository

import org.springframework.data.jpa.repository.JpaRepository
import projets.guesswho.backend.entity.Character

interface CharacterRepository : JpaRepository<Character, Long> {
    fun existsByName(name: String): Boolean
    fun findByName(name: String): Character?
}
