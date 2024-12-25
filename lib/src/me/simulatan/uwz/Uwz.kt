package me.simulatan.uwz

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import me.simulatan.uwz.polygon.Point

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

suspend fun getWarningLevel(point: Point): String {
	val geojsonBytes = client.get("https://uwz.at/data/warnings/AT/AT_today_all.geojson?cache=${Clock.System.now().epochSeconds}")
		.readRawBytes()
	val geojson: FeatureCollection = jsonCodec.decodeFromString(geojsonBytes.decodeToString())

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

	return level
}
