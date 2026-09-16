package dev.husainmukadam.flutteradb.ui

import com.android.ddmlib.IDevice
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.SimpleListCellRenderer

object DeviceChooser {

    fun label(device: IDevice): String {
        val model = device.getProperty(IDevice.PROP_DEVICE_MODEL) ?: device.name
        return "$model (${device.serialNumber})"
    }

    /** Shows a chooser on the EDT; callback receives one device or all. */
    fun choose(project: Project, devices: List<IDevice>, onChosen: (List<IDevice>) -> Unit) {
        ApplicationManager.getApplication().invokeLater({
            val allLabel = "All devices (${devices.size})"
            val items: List<Any> = listOf<Any>(allLabel) + devices
            JBPopupFactory.getInstance()
                .createPopupChooserBuilder(items)
                .setTitle("Select Device")
                .setRenderer(SimpleListCellRenderer.create("") { item: Any ->
                    if (item is IDevice) label(item) else item.toString()
                })
                .setItemChosenCallback { item -> onChosen(if (item is IDevice) listOf(item) else devices) }
                .createPopup()
                .showCenteredInCurrentWindow(project)
        }, project.disposed)
    }
}
