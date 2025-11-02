package me.dirtydan16.betterjump.actions.navigation.inside_a_file.comma

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import me.dirtydan16.betterjump.utils.extensions.lastIndexOfOrNull

open class GoToNextComma : AnAction() {
    override fun actionPerformed(event: AnActionEvent) = goToNextComma(event)
}

open class GoToPrevComma : AnAction() {
    override fun actionPerformed(event: AnActionEvent) = goToPrevComma(event)
}

fun goToNextComma(event: AnActionEvent) {
    val editor: Editor = event.getData(CommonDataKeys.EDITOR) ?: return
    val carets = editor.caretModel.allCarets
    val text = editor.document.charsSequence

    carets.forEach { caret ->
        val nextCommaIndex = text.indexOf(',', caret.offset)

        // If there is no comma to jump to, just loop to the very start of the file
        if (nextCommaIndex == -1) {
            caret.moveToOffset(text.indexOfFirst { it.isWhitespace().not() })
            editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        } else {
            moveCursorToComma(nextCommaIndex, editor, caret)
        }
    }
}

fun goToPrevComma(event: AnActionEvent) {
    val editor: Editor = event.getData(CommonDataKeys.EDITOR) ?: return
    val carets = editor.caretModel.allCarets
    val text = editor.document.charsSequence

    carets.forEach { caret ->
        // Find the comma directly before the caret. If there is none, loop backwards to the last comma
        val curCommaIndex = text.lastIndexOfOrNull(',', caret.offset - 1) ?: run {
            val lastCommaIndex = text.indexOfLast { it == ',' }
            val firstNonWhitespacePos = (lastCommaIndex..text.lastIndex).first { text[it].isWhitespace().not() }
            caret.moveToOffset(firstNonWhitespacePos)
            editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
            return@forEach
        }

        // From that position, find the *previous* comma before it
        // If there is no comma to jump to, jump to the first non-white-space character from the start of the file
        val prevCommaIndex = text.lastIndexOfOrNull(',', curCommaIndex - 1) ?: run {
            //edge case: if the very first character is not a white-space, just move the cursor to the start.
            if (text[0].isWhitespace().not()) {
                caret.removeSelection()
                caret.moveToOffset(0)
                editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
                // otherwise, move the caret to the first non-white space character
            } else {
                moveCursorToComma(0, editor, caret)
            }
            return@forEach
        }

        moveCursorToComma(prevCommaIndex, editor, caret)
    }
}

/**
 * Moves the caret to the specified comma index.
 */
fun moveCursorToComma(commaIndex: Int, editor: Editor, caret: Caret) {
    // for consistency and expected behavior
    caret.removeSelection()

    // increment by 1 to move after the comma (so we will not get stuck on the same comma)
    var commaIndex = commaIndex + 1

    val text = editor.document.charsSequence

    val findFirstNonWhiteSpaceChar: Int = text.subSequence(commaIndex..text.lastIndex)
        .indexOfFirst { ch -> !ch.isWhitespace() }

    if (findFirstNonWhiteSpaceChar != -1)
        commaIndex += findFirstNonWhiteSpaceChar


    caret.moveToOffset(commaIndex)
    editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
}

