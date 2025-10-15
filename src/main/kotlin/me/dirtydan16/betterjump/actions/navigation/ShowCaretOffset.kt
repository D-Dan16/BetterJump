package me.dirtydan16.betterjump.actions.navigation

import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import java.awt.Component

/**
 * A status bar widget that displays the current caret offset in the editor. This means the amount of characters the caret is away from the very start of the file.
 */
class CaretOffsetWidget(project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation {
    private var text = "Offset: 0"
    private var statusBar: StatusBar? = null

    init {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        editor?.caretModel?.addCaretListener(object : CaretListener {
            override fun caretPositionChanged(event: CaretEvent) {
                text = "Offset: ${event.caret?.offset ?: 0}"
                statusBar?.updateWidget(ID())
            }
        })
    }

    override fun ID() = "CaretOffsetWidget"
    override fun install(statusBar: StatusBar) { this.statusBar = statusBar }
    override fun dispose() {}
    override fun getTooltipText() = "Current caret offset"
    override fun getText() = text
    override fun getAlignment() = Component.CENTER_ALIGNMENT
    override fun getClickConsumer() = null
    override fun getPresentation() = this
}


class CaretOffsetWidgetFactory : StatusBarWidgetFactory {
    override fun createWidget(project: Project) = CaretOffsetWidget(project)
    override fun getId() = "CaretOffsetWidget"
    override fun getDisplayName() = "Caret Offset"
    override fun isAvailable(project: Project) = true
    override fun disposeWidget(widget: StatusBarWidget) {}
    override fun canBeEnabledOn(statusBar: StatusBar) = true
}

