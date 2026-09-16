package dev.husainmukadam.flutteradb.actions

import com.android.ddmlib.IDevice
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import dev.husainmukadam.flutteradb.adb.AdbBridgeProvider
import dev.husainmukadam.flutteradb.adb.AdbException
import dev.husainmukadam.flutteradb.adb.PackageResolver
import dev.husainmukadam.flutteradb.ui.DeviceChooser
import dev.husainmukadam.flutteradb.util.Notifier
import dev.husainmukadam.flutteradb.util.runInBackground

abstract class FlutterAdbAction(private val title: String) : DumbAwareAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        runInBackground(project, "Flutter ADB: $title") {
            val pkg = PackageResolver.resolve(project)
                ?: throw AdbException("Package not found. Use Tools → Flutter ADB → Set Package Name.")
            val devices = AdbBridgeProvider.onlineDevices(project)
            when (devices.size) {
                0 -> Notifier.warn(project, "No online devices.")
                1 -> execute(project, devices, pkg)
                else -> DeviceChooser.choose(project, devices) { chosen ->
                    runInBackground(project, "Flutter ADB: $title") { execute(project, chosen, pkg) }
                }
            }
        }
    }

    private fun execute(project: Project, devices: List<IDevice>, pkg: String) {
        devices.forEach { device ->
            val result = runCatching { perform(device, pkg) }.getOrElse { "failed (${it.message})" }
            Notifier.info(project, "$title · $pkg · ${DeviceChooser.label(device)}: $result")
        }
    }

    protected abstract fun perform(device: IDevice, pkg: String): String
}
