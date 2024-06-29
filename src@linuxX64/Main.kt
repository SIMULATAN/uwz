import hassio.sendWarningState
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import okio.FileSystem
import okio.Path.Companion.toPath


val jsonCodec = Json {
	ignoreUnknownKeys = true
	serializersModule += SerializersModule {
		polymorphic(Geometry::class) {
			subclass(Geometry.MultiPolygon::class, Geometry.MultiPolygon.serializer())
			subclass(Geometry.Polygon::class, Geometry.Polygon.serializer())
		}
	}
}
val client = HttpClient {
	install(ContentNegotiation) {
		json(jsonCodec)
	}
}

fun main() = runBlocking {
	val config: Config = FileSystem.SYSTEM.read("config.json".toPath()) {
		val content = readUtf8()
		jsonCodec.decodeFromString(content)
	}

	val geojsonBytes = client.get("https://uwz.at/data/warnings/AT/AT_today_all.geojson?cache=${Clock.System.now().epochSeconds}")
		.readBytes()
	val geojson: FeatureCollection = jsonCodec.decodeFromString(String(geojsonBytes))

	val point = config.point

	val matching = geojson.features.sortedByDescending { it.properties.level }.firstOrNull {
		when (val geometry = it.geometry) {
			is Geometry.Polygon -> pointInPolygon(point, geometry)
			is Geometry.MultiPolygon -> pointInMultiPolygon(point, geometry)
		}
	}
	val level = when {
		matching == null -> "No warning"
		matching.properties.level == 0 -> "Information"
		matching.properties.level == 1 -> "Warning"
		matching.properties.level == 2 -> "Severe warning"
		matching.properties.level == 3 -> "Weltuntergang"
		else -> "Unknown warning level"
	}

	println("Warning level: $level")
	if (config.homeassistant != null) {
		sendWarningState(config.homeassistant, level)
	}
}
