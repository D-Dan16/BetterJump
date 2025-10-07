package me.dirtydan16.betterjump.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType

open class GoToNextComma : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor: Editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val carets = editor.caretModel.allCarets

        carets.forEach {
            val nextCommaIndex = editor.document.charsSequence.indexOf(
                ',',
                it.offset,
            )

            goToComma(nextCommaIndex, editor,it)
        }
    }
}

open class GoToPrevComma : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor: Editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val carets = editor.caretModel.allCarets
        val chars = editor.document.charsSequence

        carets.forEach {
            // Find the comma directly before the caret
            val curCommaIndex = (it.offset - 1 downTo 0).firstOrNull { i -> chars[i] == ',' } ?: -1

            // From that position, find the *previous* comma before it
            val prevCommaIndex = (curCommaIndex - 1 downTo 0).firstOrNull { i -> chars[i] == ',' } ?: -1

            goToComma(prevCommaIndex, editor, it)
        }
    }
}


/**
 * Moves the caret to the specified comma index.
 */
private fun goToComma(commaIndex: Int, editor: Editor, caret: Caret) {
    if (commaIndex == -1)
        return

    // increment by 1 to move after the comma (so we will not get stuck on the same comma)
    var commaIndex = commaIndex+1


    val text = editor.document.charsSequence

    val findFirstNonWhiteSpaceChar: Int = text.subSequence(commaIndex..text.lastIndex)
        .indexOfFirst { ch -> !ch.isWhitespace() }

    if (findFirstNonWhiteSpaceChar != -1)
        commaIndex += findFirstNonWhiteSpaceChar


    caret.moveToOffset(commaIndex)
    editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
}
