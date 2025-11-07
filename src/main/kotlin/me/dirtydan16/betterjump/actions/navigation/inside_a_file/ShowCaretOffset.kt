package me.dirtydan16.betterjump.actions.navigation.inside_a_file

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import java.awt.Component

/**
 * A status bar widget that displays the current caret offset in the editor. This means the number of characters in the caret is away from the very start of the file.
 */
class CaretOffsetWidget(val project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation, Disposable {
    private var text = "Offset: -"
    private var statusBar: StatusBar? = null

    override fun ID() = "CaretOffsetWidget"

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar

        // Initialize from current editor (may be null).
        updateFromEditor(FileEditorManager.getInstance(project).selectedTextEditor)

        // Update when the selected editor changes.
        project.messageBus.connect(this).subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            object : FileEditorManagerListener {
                override fun selectionChanged(event: FileEditorManagerEvent) {
                    updateFromEditor(FileEditorManager.getInstance(project).selectedTextEditor)
                }
            }
        )

        // Update when the caret moves in any editor; only react if it's the currently selected one.
        EditorFactory.getInstance().eventMulticaster.addCaretListener(object : CaretListener {
            override fun caretPositionChanged(event: CaretEvent) {
                val selected = FileEditorManager.getInstance(project).selectedTextEditor
                if (event.editor == selected) {
                    text = "Offset: ${event.caret?.offset ?: "-"}"
                    statusBar.updateWidget(ID())
                }
            }
        }, this)


    }

    override fun dispose() { statusBar = null }
    override fun getTooltipText() = "Current caret offset"
    override fun getText() = text
    override fun getAlignment() = Component.CENTER_ALIGNMENT
    override fun getClickConsumer() = null
    override fun getPresentation() = this

    private fun updateFromEditor(editor: Editor?) {
        val offset = editor?.caretModel?.currentCaret?.offset
        text = "Offset: ${offset ?: "-"}"
        statusBar?.updateWidget(ID())
    }


}


class CaretOffsetWidgetFactory : StatusBarWidgetFactory {
    override fun createWidget(project: Project) = CaretOffsetWidget(project)
    override fun getId() = "CaretOffsetWidget"
    override fun getDisplayName() = "Caret Offset"
    override fun isAvailable(project: Project) = true
    override fun disposeWidget(widget: StatusBarWidget) {}
    override fun canBeEnabledOn(statusBar: StatusBar) = true
}

