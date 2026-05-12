package projets.guesswho.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "genshin_character")
class Character(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String,

    var imageUrl: String? = null
)
