package projets.guesswho.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "player")
class Player(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    val password: String?
)
