package dev.husainmukadam.flutteradb.actions

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.popup.JBPopupFactory

class QuickListAction : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val group = ActionManager.getInstance().getAction("FlutterAdb.Group") as? ActionGroup ?: return
        JBPopupFactory.getInstance()
            .createActionGroupPopup("Flutter ADB", group, e.dataContext, JBPopupFactory.ActionSelectionAid.NUMBERING, false)
            .showInBestPositionFor(e.dataContext)
    }
}
