package hassio

import HomeAssistantConfig
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import jsonCodec
import kotlinx.serialization.Serializable

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
