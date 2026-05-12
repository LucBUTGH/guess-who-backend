package projets.guesswho.backend.repository

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import projets.guesswho.backend.entity.Game
import projets.guesswho.backend.entity.Player

interface GameRepository : JpaRepository<Game, Long> {
    fun findByCode(code: String): Game?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Game g WHERE g.code = :code")
    fun findByCodeForUpdate(@Param("code") code: String): Game?

    @Query("SELECT g FROM Game g WHERE (g.player1 = :player OR g.player2 = :player) AND g.status = projets.guesswho.backend.entity.GameStatus.IN_PROGRESS")
    fun findInProgressByPlayer(@Param("player") player: Player): Game?

    @Query("SELECT g FROM Game g WHERE (g.player1 = :player OR g.player2 = :player) AND g.status = projets.guesswho.backend.entity.GameStatus.FINISHED AND g.winner IS NOT NULL ORDER BY g.id DESC")
    fun findFinishedGamesByPlayer(@Param("player") player: Player): List<Game>
}
