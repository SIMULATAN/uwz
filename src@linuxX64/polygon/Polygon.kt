package polygon

/**
 * The 2D polygon.
 *
 * Adapted from [polygon-contains-point](https://github.com/sromku/polygon-contains-point)
 *
 * @author Roman Kushnarenko (sromku@gmail.com)
 */
class Polygon private constructor(private val sides: List<Line>, private val boundingBox: BoundingBox) {

	/**
	 * Builder of the polygon
	 *
	 * @author Roman Kushnarenko (sromku@gmail.com)
	 */
	class Builder {
		private var vertices: MutableList<Point> = ArrayList()
		private val sides: MutableList<Line> = ArrayList()
		private var boundingBox: BoundingBox? = null
		private var firstPoint = true
		private var isClosed = false

		/**
		 * Add vertex points of the polygon.<br></br>
		 * It is very important to add the vertexes by order, like you were drawing them one by one.
		 *
		 * @param point The vertex point
		 * @return The builder
		 */
		fun addVertex(point: Point): Builder {
			if (isClosed) {
				// each hole we start with the new array of vertex points
				vertices = ArrayList()
				isClosed = false
			}

			updateBoundingBox(point)
			vertices.add(point)
			// add line (edge) to the polygon
			if (vertices.size > 1) {
				sides.add(Line(vertices[vertices.size - 2], point))
			}

			return this
		}

		/**
		 * Close the polygon shape. This will create a new side (edge) from the **last** vertex point to the **first** vertex point.
		 *
		 * @return The builder
		 */
		fun close(): Builder {
			validate()
			// add last Line
			sides.add(Line(vertices[vertices.size - 1], vertices[0]))
			isClosed = true

			return this
		}

		/**
		 * Build the instance of the polygon shape.
		 *
		 * @return The polygon
		 */
		fun build(): Polygon {
			validate()
			// in case you forgot to close
			if (!isClosed) {
				// add last Line
				sides.add(Line(vertices[vertices.size - 1], vertices[0]))
			}
			val polygon = Polygon(sides, boundingBox!!)
			return polygon
		}

		/**
		 * Update bounding box with a new point.<br></br>
		 *
		 * @param point New point
		 */
		private fun updateBoundingBox(point: Point) {
			if (firstPoint) {
				boundingBox = BoundingBox()
				boundingBox!!.xMax = point.long
				boundingBox!!.xMin = point.long
				boundingBox!!.yMax = point.lat
				boundingBox!!.yMin = point.lat

				firstPoint = false
			} else {
				// set bounding box
				if (point.long > boundingBox!!.xMax) {
					boundingBox!!.xMax = point.long
				} else if (point.long < boundingBox!!.xMin) {
					boundingBox!!.xMin = point.long
				}
				if (point.lat > boundingBox!!.yMax) {
					boundingBox!!.yMax = point.lat
				} else if (point.lat < boundingBox!!.yMin) {
					boundingBox!!.yMin = point.lat
				}
			}
		}

		private fun validate() {
			if (vertices.size < 3) {
				throw RuntimeException("Polygon must have at least 3 points")
			}
		}
	}

	/**
	 * Check if the the given point is inside of the polygon.<br></br>
	 *
	 * @param point The point to check
	 * @return `True` if the point is inside the polygon, otherwise return `False`
	 */
	fun contains(point: Point): Boolean {
		if (inBoundingBox(point)) {
			val ray: Line = createRay(point)
			var intersection = 0
			for (side in sides) {
				if (intersect(ray, side)) {
					intersection++
				}
			}
			/*
             * If the number of intersections is odd, then the point is inside the polygon
			 */
			if (intersection % 2 != 0) {
				return true
			}
		}
		return false
	}

	/**
	 * By given ray and one side of the polygon, check if both lines intersect.
	 *
	 * @param ray
	 * @param side
	 * @return `True` if both lines intersect, otherwise return `False`
	 */
	private fun intersect(ray: Line, side: Line): Boolean {
		// if both vectors aren't from the kind of x=1 lines then go into
		val intersectPoint = when {
			!ray.isVertical && !side.isVertical -> {
				// check if both vectors are parallel. If they are parallel then no intersection point will exist
				if (ray.a - side.b == 0.0) {
					return false
				}
				val x: Double = ((side.b - ray.b) / (ray.a - side.a)) // x = (b2-b1)/(a1-a2)
				val y: Double = side.a * x + side.b // y = a2*x+b2
				Point(x, y)
			}
			ray.isVertical && !side.isVertical -> {
				val x: Double = ray.start.long
				val y: Double = side.a * x + side.b
				Point(x, y)
			}
			!ray.isVertical && side.isVertical -> {
				val x: Double = side.start.long
				val y: Double = ray.a * x + ray.b
				Point(x, y)
			}
			else -> {
				return false
			}
		}
		return side.isInside(intersectPoint) && ray.isInside(intersectPoint)
	}

	/**
	 * Create a ray. The ray will be created by given point and on point outside of the polygon.<br></br>
	 * The outside point is calculated automatically.
	 *
	 * @param point
	 * @return
	 */
	private fun createRay(point: Point): Line {
		// create outside point
		val epsilon = (boundingBox.xMax - boundingBox.xMin) / 10e6
		val outsidePoint = Point(boundingBox.xMin - epsilon, boundingBox.yMin)
		val vector = Line(outsidePoint, point)
		return vector
	}

	/**
	 * Check if the given point is in bounding box
	 *
	 * @param point
	 * @return `True` if the point in bounding box, otherwise return `False`
	 */
	private fun inBoundingBox(point: Point): Boolean {
		return !(
			point.long < boundingBox.xMin
			|| point.long > boundingBox.xMax
			|| point.lat < boundingBox.yMin
			|| point.lat > boundingBox.yMax
		)
	}

	class BoundingBox {
		var xMax: Double = Double.NEGATIVE_INFINITY
		var xMin: Double = Double.NEGATIVE_INFINITY
		var yMax: Double = Double.NEGATIVE_INFINITY
		var yMin: Double = Double.NEGATIVE_INFINITY
	}
}
