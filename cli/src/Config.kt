import kotlinx.serialization.Serializable
import me.simulatan.uwz.polygon.Point

@Serializable
data class Config(
	val point: Point,

	val homeassistant: HomeAssistantConfig? = null,
)

@Serializable
data class HomeAssistantConfig(
	val host: String,
	val token: String,
	val entity: String,
)
