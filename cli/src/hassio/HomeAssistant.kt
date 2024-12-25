package hassio

import HomeAssistantConfig
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import me.simulatan.uwz.jsonCodec

val client = HttpClient {
	install(ContentNegotiation) {
		json(jsonCodec)
	}
}

@Serializable
data class SensorState(val state: String)

suspend fun sendWarningState(config: HomeAssistantConfig, status: String) {
	val result = client.post("${config.host}/api/states/${config.entity}") {
		header(HttpHeaders.Authorization, "Bearer ${config.token}")
		header(HttpHeaders.ContentType, ContentType.Application.Json)
		setBody(SensorState(status))
	}
	if (!result.status.isSuccess()) {
		println("Failed to send state: ${result.status} - ${result.bodyAsText()}")
	}
}
