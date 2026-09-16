package dev.husainmukadam.flutteradb.adb

import com.android.tools.idea.sdk.IdeSdks
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import java.io.File
import java.util.Properties

/** Finds adb without relying on an Android facet. */
object AdbLocator {

    fun find(project: Project): File? {
        val exe = if (SystemInfo.isWindows) "adb.exe" else "adb"
        return sdkCandidates(project)
            .map { File(it, "platform-tools/$exe") }
            .firstOrNull { it.isFile }
    }

    private fun sdkCandidates(project: Project): Sequence<File> = sequence {
        runCatching { IdeSdks.getInstance().androidSdkPath }.getOrNull()?.let { yield(it) }
        localPropertiesSdk(project)?.let { yield(it) }
        listOf("ANDROID_HOME", "ANDROID_SDK_ROOT")
            .mapNotNull { System.getenv(it) }
            .forEach { yield(File(it)) }
    }

    private fun localPropertiesSdk(project: Project): File? {
        val file = project.basePath?.let { File(it, "android/local.properties") } ?: return null
        if (!file.isFile) return null
        val props = Properties().apply { file.inputStream().use { load(it) } }
        return props.getProperty("sdk.dir")?.let(::File)
    }
}
