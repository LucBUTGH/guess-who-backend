package projets.guesswho.backend.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import projets.guesswho.backend.dto.CharacterDto
import projets.guesswho.backend.repository.CharacterRepository

@RestController
@RequestMapping("/api/characters")
class CharacterController(private val characterRepository: CharacterRepository) {

    @GetMapping
    fun getAll(): ResponseEntity<List<CharacterDto>> {
        val characters = characterRepository.findAll().map {
            CharacterDto(id = it.id, name = it.name, imageUrl = it.imageUrl)
        }
        return ResponseEntity.ok(characters)
    }
}
