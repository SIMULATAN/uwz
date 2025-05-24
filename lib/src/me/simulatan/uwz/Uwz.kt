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

suspend fun getWarningLevel(point: Point): WarningLevel {
	val geojsonBytes = client.get("https://uwz.at/data/warnings/AT/AT_today_all.geojson?cache=${Clock.System.now().epochSeconds}")
		.readRawBytes()
	val geojson: FeatureCollection = jsonCodec.decodeFromString(geojsonBytes.decodeToString())

	val matching = geojson.features.sortedByDescending { it.properties.level }.firstOrNull {
		when (val geometry = it.geometry) {
			is Geometry.Polygon -> pointInPolygon(point, geometry)
			is Geometry.MultiPolygon -> pointInMultiPolygon(point, geometry)
		}
	}

	return WarningLevel.entries.firstOrNull { it.level == matching?.properties?.level }
		?: WarningLevel.NO_WARNING
}
