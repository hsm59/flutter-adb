package dev.husainmukadam.flutteradb.actions

import com.android.ddmlib.IDevice
import dev.husainmukadam.flutteradb.adb.AdbCommands

private fun status(ok: Boolean) = if (ok) "done" else "failed"

class UninstallAction : FlutterAdbAction("Uninstall") {
    override fun perform(device: IDevice, pkg: String): String =
        AdbCommands.uninstall(device, pkg)?.let { "failed ($it)" } ?: "done"
}

class KillAction : FlutterAdbAction("Kill") {
    override fun perform(device: IDevice, pkg: String): String {
        AdbCommands.kill(device, pkg)
        return "done"
    }
}

class StartAction : FlutterAdbAction("Start") {
    override fun perform(device: IDevice, pkg: String): String =
        status(AdbCommands.start(device, pkg))
}

class RestartAction : FlutterAdbAction("Restart") {
    override fun perform(device: IDevice, pkg: String): String {
        AdbCommands.kill(device, pkg)
        return status(AdbCommands.start(device, pkg))
    }
}

class ClearDataAction : FlutterAdbAction("Clear data") {
    override fun perform(device: IDevice, pkg: String): String =
        status(AdbCommands.clearData(device, pkg))
}

class ClearDataAndRestartAction : FlutterAdbAction("Clear data and restart") {
    override fun perform(device: IDevice, pkg: String): String =
        status(AdbCommands.clearData(device, pkg) && AdbCommands.start(device, pkg))
}

class GrantPermissionsAction : FlutterAdbAction("Grant permissions") {
    override fun perform(device: IDevice, pkg: String): String {
        val (updated, total) = AdbCommands.setPermissions(device, pkg, grant = true)
        return "$updated/$total granted (runtime only)"
    }
}

class RevokePermissionsAction : FlutterAdbAction("Revoke permissions") {
    override fun perform(device: IDevice, pkg: String): String {
        val (updated, total) = AdbCommands.setPermissions(device, pkg, grant = false)
        return "$updated/$total revoked (runtime only)"
    }
}
