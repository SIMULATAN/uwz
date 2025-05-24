import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.promise
import me.simulatan.uwz.getWarningLevel
import me.simulatan.uwz.polygon.Point
import org.w3c.dom.url.URLSearchParams
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit

internal fun error(message: String): Response {
	val responseHeaders: dynamic = object {}
	responseHeaders["content-type"] = "text/plain"

	return Response(message, ResponseInit(status = 400, headers = responseHeaders))
}

internal fun fetch(request: Request) = CoroutineScope(Dispatchers.Main).promise {
	val search = request.url
		.substringAfter("//") // Remove protocol (e.g., "https://")
		.substringAfter("/") // Remove domain (e.g., "example.com/")
	val params = URLSearchParams(search)

	val lat = params.get("lat")?.toDouble() ?: return@promise error("Missing or invalid 'lat' parameter")
	val long = params.get("long")?.toDouble() ?: return@promise error("Missing or invalid 'long' parameter")

	val warningLevel = getWarningLevel(Point(lat = lat, long = long))

	val responseHeaders: dynamic = object {}
	responseHeaders["content-type"] = "text/json"

	Response(warningLevel, ResponseInit(headers = responseHeaders))
}
