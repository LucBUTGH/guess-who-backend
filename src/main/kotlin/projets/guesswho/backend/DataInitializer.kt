package projets.guesswho.backend

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import projets.guesswho.backend.entity.Character
import projets.guesswho.backend.repository.CharacterRepository
import tools.jackson.databind.ObjectMapper

data class CharacterSeedData(val characters: List<String>)

@Component
class DataInitializer(
    private val characterRepository: CharacterRepository,
    private val objectMapper: ObjectMapper,
    @Value("\${app.base-url}") private val baseUrl: String
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val resource = ClassPathResource("characters.json")
        val data = objectMapper.readValue(resource.inputStream, CharacterSeedData::class.java)

        data.characters.forEach { name ->
            val imageUrl = "$baseUrl/images/${name.lowercase().replace(" ", "_")}.png"
            val existing = characterRepository.findByName(name)
            if (existing == null) {
                characterRepository.save(Character(name = name, imageUrl = imageUrl))
            } else if (existing.imageUrl != imageUrl) {
                existing.imageUrl = imageUrl
                characterRepository.save(existing)
            }
        }
    }
}
