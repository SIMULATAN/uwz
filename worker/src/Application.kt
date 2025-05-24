import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.promise
import me.simulatan.uwz.WarningLevel
import me.simulatan.uwz.getWarningLevel
import me.simulatan.uwz.polygon.Point
import org.w3c.dom.url.URL
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit
import kotlin.js.json

internal fun error(status: Short = 400, message: String): Response {
	val responseHeaders: dynamic = object {}
	responseHeaders["content-type"] = "text/plain"

	return Response(message, ResponseInit(status = status, headers = responseHeaders))
}

internal fun fetch(request: Request) = CoroutineScope(Dispatchers.Main).promise {
	val url = URL(request.url)
	val params = url.searchParams

	val path = url.pathname

	suspend fun respond(contentType: String, transform: (suspend (WarningLevel) -> String)): Response {
		val responseHeaders: dynamic = object {}
		responseHeaders["content-type"] = contentType

		val lat = params.get("lat")?.toDouble() ?: return error("Missing or invalid 'lat' parameter")
		val long = params.get("long")?.toDouble() ?: return error("Missing or invalid 'long' parameter")

		val warningLevel = getWarningLevel(Point(lat = lat, long = long))

		return Response(transform(warningLevel), ResponseInit(headers = responseHeaders))
	}

	val responseHeaders: dynamic = object {}
	when (path.removePrefix("/")) {
		"level/json" -> respond("application/json") {
			val response = json(
				"level" to it.level,
				"description" to it.description
			)
			JSON.stringify(response)
		}
		"level/value" -> respond("text/plain") {
			it.level?.toString() ?: "null"
		}
		"level/description" -> respond("text/plain") {
			it.description
		}
		else -> {
			responseHeaders["content-type"] = "text/plain"
			error(404, "Unknown path: $path")
		}
	}
}
