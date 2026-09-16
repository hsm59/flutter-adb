package dev.husainmukadam.flutteradb.adb

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.tools.idea.adb.AdbService
import com.intellij.openapi.project.Project
import java.util.concurrent.TimeUnit

class AdbException(message: String) : RuntimeException(message)

/** Reuses Android Studio's own bridge. Call off the EDT. */
object AdbBridgeProvider {

    fun onlineDevices(project: Project): List<IDevice> {
        val adb = AdbLocator.find(project)
            ?: throw AdbException("adb not found. Set the Android SDK in Settings or sdk.dir in android/local.properties.")
        val bridge = AdbService.getInstance().getDebugBridge(adb).get(15, TimeUnit.SECONDS)
            ?: throw AdbException("Could not connect to ADB bridge.")
        awaitDeviceList(bridge)
        return bridge.devices.filter { it.isOnline }
    }

    private fun awaitDeviceList(bridge: AndroidDebugBridge) {
        repeat(50) {
            if (bridge.hasInitialDeviceList()) return
            Thread.sleep(100)
        }
        throw AdbException("Timed out waiting for device list.")
    }
}
