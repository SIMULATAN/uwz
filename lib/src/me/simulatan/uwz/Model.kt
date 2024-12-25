package me.simulatan.uwz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Geometry {
	@Serializable
	@SerialName("MultiPolygon")
	data class MultiPolygon(val coordinates: List<List<List<Coordinate>>>) : Geometry()
	@Serializable
	@SerialName("Polygon")
	data class Polygon(val coordinates: List<List<Coordinate>>) : Geometry()
}

typealias Coordinate = List<Double>
val Coordinate.lat
	get() = this[1]
val Coordinate.long
	get() = this[0]

@Serializable
data class FeatureProperties(val level: Int)
@Serializable
data class Feature(val properties: FeatureProperties, val geometry: Geometry)
@Serializable
data class FeatureCollection(val features: List<Feature>)
