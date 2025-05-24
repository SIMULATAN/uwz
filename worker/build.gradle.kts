@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

plugins {
	kotlin("multiplatform")
}

repositories {
	mavenCentral()
}
val kotlinJsOutputFilename = "worker-app.js"
kotlin {
	targets
		.filterIsInstance<KotlinJsTargetDsl>()
		.forEach { target ->
			target.apply {
				browser {
					webpackTask {
						mainOutputFileName = kotlinJsOutputFilename
					}
				}
				binaries.executable()
			}
		}
}

tasks {
	named("jsBrowserProductionWebpack") {
		val entrypointFile = "${layout.buildDirectory.asFile.get()}/dist/js/productionExecutable/entrypoint.js"
		outputs.file(entrypointFile)
		val jsEntrypoint = """
            // Import the compiled Kotlin/JS module
            import app from './$kotlinJsOutputFilename';
        
            // The entrypoint expected by Cloudflare
            export default {
              async fetch(request, env, ctx) {
                return app.main(request, env, ctx);
              },
            };
        """.trimIndent()

		doLast {
			File(entrypointFile).writeText(jsEntrypoint)
		}
	}
}
