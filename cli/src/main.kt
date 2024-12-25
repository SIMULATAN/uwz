import hassio.sendWarningState
import kotlinx.coroutines.runBlocking
import me.simulatan.uwz.getWarningLevel
import me.simulatan.uwz.jsonCodec
import okio.FileSystem
import okio.Path.Companion.toPath

fun main() = runBlocking {
	val config: Config = FileSystem.SYSTEM.read("config.json".toPath()) {
		val content = readUtf8()
		jsonCodec.decodeFromString(content)
	}

	val level = getWarningLevel(config.point)

	println("Warning level: $level")
	if (config.homeassistant != null) {
		sendWarningState(config.homeassistant, level)
	}
}
