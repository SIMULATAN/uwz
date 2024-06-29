import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import polygon.Point

@Serializable
data class Config(
	@Serializable(with = PointSerializer::class)
	val point: Point,

	val homeassistant: HomeAssistantConfig? = null
)

@Serializable
data class HomeAssistantConfig(
	val host: String,
	val token: String,
	val entity: String
)


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Point::class)
object PointSerializer
