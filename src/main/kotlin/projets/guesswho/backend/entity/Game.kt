package projets.guesswho.backend.entity

import jakarta.persistence.*

enum class GameStatus { WAITING, IN_PROGRESS, FINISHED }

@Entity
@Table(name = "game")
class Game(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val code: String,

    @ManyToOne
    @JoinColumn(name = "player1_id")
    val player1: Player,

    @ManyToOne
    @JoinColumn(name = "player2_id")
    var player2: Player? = null,

    @ManyToOne
    @JoinColumn(name = "player1_character_id")
    var player1Character: Character? = null,

    @ManyToOne
    @JoinColumn(name = "player2_character_id")
    var player2Character: Character? = null,

    @ManyToOne
    @JoinColumn(name = "winner_id")
    var winner: Player? = null,

    @Enumerated(EnumType.STRING)
    var status: GameStatus = GameStatus.WAITING
)
