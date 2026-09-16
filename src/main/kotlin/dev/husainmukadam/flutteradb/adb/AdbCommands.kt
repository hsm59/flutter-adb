package dev.husainmukadam.flutteradb.adb

import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
import java.util.concurrent.TimeUnit

object AdbCommands {
    private const val TIMEOUT_SECONDS = 20L
    private val PERMISSION_LINE = Regex("""^[A-Za-z][\w.]*\.[\w.]+(:.*)?$""")

    fun shell(device: IDevice, command: String): String {
        val receiver = CollectingOutputReceiver()
        device.executeShellCommand(command, receiver, TIMEOUT_SECONDS, TimeUnit.SECONDS)
        return receiver.output.trim()
    }

    /** Returns null on success, error message otherwise. */
    fun uninstall(device: IDevice, pkg: String): String? = device.uninstallPackage(pkg)

    fun kill(device: IDevice, pkg: String) {
        shell(device, "am force-stop $pkg")
    }

    fun start(device: IDevice, pkg: String): Boolean =
        !shell(device, "monkey -p $pkg -c android.intent.category.LAUNCHER 1").contains("No activities found")

    fun clearData(device: IDevice, pkg: String): Boolean =
        shell(device, "pm clear $pkg").contains("Success")

    /** Returns (updated, requested). Non-runtime permissions fail silently. */
    fun setPermissions(device: IDevice, pkg: String, grant: Boolean): Pair<Int, Int> {
        val permissions = requestedPermissions(device, pkg)
        val verb = if (grant) "grant" else "revoke"
        val updated = permissions.count { shell(device, "pm $verb $pkg $it").isBlank() }
        return updated to permissions.size
    }

    private fun requestedPermissions(device: IDevice, pkg: String): List<String> {
        val lines = shell(device, "dumpsys package $pkg").lines()
        val start = lines.indexOfFirst { it.trim() == "requested permissions:" }
        if (start < 0) return emptyList()
        return lines.drop(start + 1)
            .map { it.trim() }
            .takeWhile { PERMISSION_LINE.matches(it) }
            .map { it.substringBefore(':').substringBefore(',') }
            .distinct()
    }
}
