package dev.husainmukadam.flutteradb.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import dev.husainmukadam.flutteradb.adb.PackageResolver
import dev.husainmukadam.flutteradb.settings.FlutterAdbSettings

class SetPackageAction : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val settings = FlutterAdbSettings.getInstance(project)
        val detected = PackageResolver.detect(project).orEmpty()
        val input = Messages.showInputDialog(
            project,
            "Package name (clear to use auto-detected: ${detected.ifEmpty { "none" }}):",
            "Flutter ADB",
            null,
            settings.packageOverride.ifEmpty { detected },
            null,
        ) ?: return
        settings.packageOverride = input.trim().takeUnless { it == detected }.orEmpty()
    }
}
