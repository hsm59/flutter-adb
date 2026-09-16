package dev.husainmukadam.flutteradb.adb

import com.intellij.openapi.project.Project
import dev.husainmukadam.flutteradb.settings.FlutterAdbSettings
import java.io.File

/** Resolves the app id from the Flutter project's android/ folder. */
object PackageResolver {
    private val APP_ID = Regex("""applicationId\s*=?\s*["']([\w.]+)["']""")
    private val NAMESPACE = Regex("""namespace\s*=?\s*["']([\w.]+)["']""")
    private val MANIFEST_PACKAGE = Regex("""package\s*=\s*"([\w.]+)"""")

    fun resolve(project: Project): String? =
        FlutterAdbSettings.getInstance(project).packageOverride.trim().ifEmpty { null } ?: detect(project)

    fun detect(project: Project): String? {
        val appDir = project.basePath?.let { File(it, "android/app") } ?: return null

        val gradle = listOf("build.gradle.kts", "build.gradle")
            .map { File(appDir, it) }
            .firstOrNull { it.isFile }
            ?.readText()
        gradle?.let { text ->
            (APP_ID.find(text) ?: NAMESPACE.find(text))?.let { return it.groupValues[1] }
        }

        return File(appDir, "src/main/AndroidManifest.xml")
            .takeIf { it.isFile }
            ?.readText()
            ?.let { MANIFEST_PACKAGE.find(it)?.groupValues?.get(1) }
    }
}
