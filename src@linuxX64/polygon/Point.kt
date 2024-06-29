package polygon

/**
 * Point on 2D landscape
 *
 * Adapted from [polygon-contains-point](https://github.com/sromku/polygon-contains-point)
 *
 * @author Roman Kushnarenko (sromku@gmail.com)
 */
class Point(var long: Double, var lat: Double) {
	override fun toString(): String {
		return "($long,$lat)"
	}
}
