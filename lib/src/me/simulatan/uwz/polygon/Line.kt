package me.simulatan.uwz.polygon

/**
 * Line is defined by starting point and ending point on 2D dimension.<br></br>
 *
 * Adapted from [polygon-contains-point](https://github.com/sromku/polygon-contains-point)
 *
 * @author Roman Kushnarenko (sromku@gmail.com)
 */
class Line(val start: Point, private val end: Point) {
	/**
	 * y = **A**x + B
	 *
	 * @return The **A**
	 */
	var a: Double = Double.NaN

	/**
	 * y = Ax + **B**
	 *
	 * @return The **B**
	 */
	var b: Double = Double.NaN

	/**
	 * Indicate whereas the line is vertical. <br></br>
	 * For example, line like x=1 is vertical, in other words parallel to axis Y. <br></br>
	 * In this case the A is (+/-)infinite.
	 *
	 * @return `True` if the line is vertical, otherwise return `False`
	 */
	var isVertical: Boolean = false

	init {
		if (this.end.long - this.start.long != 0.0) {
			a = ((this.end.lat - this.start.lat) / (this.end.long - this.start.long))
			b = this.start.lat - a * this.start.long
		} else {
			isVertical = true
		}
	}

	/**
	 * Indicate whereas the point lays on the line.
	 *
	 * @param point - The point to check
	 * @return `True` if the point lays on the line, otherwise return `False`
	 */
	fun isInside(point: Point): Boolean {
		val maxX: Double = if (this.start.long > this.end.long) this.start.long else this.end.long
		val minX: Double = if (this.start.long < this.end.long) this.start.long else this.end.long
		val maxY: Double = if (this.start.lat > this.end.lat) this.start.lat else this.end.lat
		val minY: Double = if (this.start.lat < this.end.lat) this.start.lat else this.end.lat

		return (point.long in minX..maxX) && (point.lat in minY..maxY)
	}

	override fun toString(): String {
		return "${this.start}-${this.end}"
	}
}
