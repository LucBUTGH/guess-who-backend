package projets.guesswho.backend.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import projets.guesswho.backend.entity.Player

interface PlayerRepository : JpaRepository<Player, Long> {
    fun findByUsername(username: String): Player?
    fun existsByUsername(username: String): Boolean

    @Query("SELECT COUNT(g) FROM Game g WHERE g.winner = :player AND g.status = projets.guesswho.backend.entity.GameStatus.FINISHED")
    fun countWins(@Param("player") player: Player): Long

    @Query("SELECT COUNT(g) FROM Game g WHERE (g.player1 = :player OR g.player2 = :player) AND g.status = projets.guesswho.backend.entity.GameStatus.FINISHED AND g.winner IS NOT NULL AND g.winner <> :player")
    fun countLosses(@Param("player") player: Player): Long

    @Query("SELECT p FROM Player p ORDER BY (SELECT COUNT(g) FROM Game g WHERE g.winner = p AND g.status = projets.guesswho.backend.entity.GameStatus.FINISHED) DESC")
    fun findTop10ByWins(pageable: Pageable): List<Player>
}
