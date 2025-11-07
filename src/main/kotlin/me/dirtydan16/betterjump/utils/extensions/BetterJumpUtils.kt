package me.dirtydan16.betterjump.utils.extensions

import com.intellij.ide.DataManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager

fun repeatAction(times: Int , action: String, editor: Editor) {
    repeat(times) {
        val handler = EditorActionManager.getInstance().getActionHandler(action)
        val dataContext = DataManager.getInstance().getDataContext(editor.contentComponent)

        handler.execute(editor, editor.caretModel.currentCaret, dataContext)
    }
}