package dev.husainmukadam.flutteradb.settings

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "FlutterAdbSettings", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class FlutterAdbSettings : SimplePersistentStateComponent<FlutterAdbSettings.SettingsState>(SettingsState()) {

    class SettingsState : BaseState() {
        var packageOverride by string("")
    }

    var packageOverride: String
        get() = state.packageOverride.orEmpty()
        set(value) { state.packageOverride = value }

    companion object {
        fun getInstance(project: Project): FlutterAdbSettings = project.service()
    }
}
