package dev.husainmukadam.flutteradb.util

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project

object Notifier {
    private fun notify(project: Project, message: String, type: NotificationType) =
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Flutter ADB")
            .createNotification(message, type)
            .notify(project)

    fun info(project: Project, message: String) = notify(project, message, NotificationType.INFORMATION)
    fun warn(project: Project, message: String) = notify(project, message, NotificationType.WARNING)
    fun error(project: Project, message: String) = notify(project, message, NotificationType.ERROR)
}

fun runInBackground(project: Project, title: String, block: () -> Unit) {
    object : Task.Backgroundable(project, title, false) {
        override fun run(indicator: ProgressIndicator) {
            try {
                block()
            } catch (e: ProcessCanceledException) {
                throw e
            } catch (e: Exception) {
                Notifier.error(project, e.message ?: e.javaClass.simpleName)
            }
        }
    }.queue()
}
