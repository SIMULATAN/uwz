import polygon.Point
import polygon.Polygon

fun pointInPolygon(point: Point, polygon: Geometry.Polygon): Boolean {
	return polygon.coordinates.any { coords ->
		return@any Polygon.Builder().apply {
			coords.forEach { addVertex(Point(it.long, it.lat)) }
			close()
		}.build().contains(point)
	}
}

fun pointInMultiPolygon(point: Point, multiPolygon: Geometry.MultiPolygon): Boolean {
	return multiPolygon.coordinates.any { polygon ->
		val inPolygon = pointInPolygon(point, Geometry.Polygon(polygon))
		return@any inPolygon
	}
}
