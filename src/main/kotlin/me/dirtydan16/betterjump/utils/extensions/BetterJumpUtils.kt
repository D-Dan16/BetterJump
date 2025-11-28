package me.dirtydan16.betterjump.utils.extensions

import com.intellij.ide.DataManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import kotlin.reflect.KClass

infix fun <T> T.castTo(classes: List<KClass<*>>): Any? {
    return classes.firstOrNull { kclass ->
        kclass.isInstance(this)
    }?.let { if (it.isInstance(this)) this else null }
}

fun repeatAction(times: Int , action: String, editor: Editor) {
    repeat(times) {
        val handler = EditorActionManager.getInstance().getActionHandler(action)
        val dataContext = DataManager.getInstance().getDataContext(editor.contentComponent)

        handler.execute(editor, editor.caretModel.currentCaret, dataContext)
    }
}