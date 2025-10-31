package me.dirtydan16.betterjump.actions.navigation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import me.dirtydan16.betterjump.utils.additions.findIndicesOfMethodCall
import me.dirtydan16.betterjump.utils.extensions.indexOfOrNull
import me.dirtydan16.betterjump.utils.extensions.lastIndexOfOrNull

open class GoToNextComma : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
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
                goToComma(nextCommaIndex, editor, caret)
            }
        }
    }
}

open class GoToPrevComma : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor: Editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val carets = editor.caretModel.allCarets
        val text = editor.document.charsSequence

        carets.forEach { caret ->
            // Find the comma directly before the caret. If there is none, loop backwards to the last comma
            val curCommaIndex = text.lastIndexOfOrNull(',',caret.offset-1) ?: run {
                val lastCommaIndex = text.indexOfLast { it == ',' }
                val firstNonWhitespacePos = (lastCommaIndex..text.lastIndex).first { text[it].isWhitespace().not() }
                caret.moveToOffset(firstNonWhitespacePos)
                editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
                return@forEach
            }

            // From that position, find the *previous* comma before it
            // If there is no comma to jump to, jump to the first non-white-space character from the start of the file
            val prevCommaIndex = text.lastIndexOfOrNull(',',curCommaIndex-1) ?: run {
                //edge case: if the very first character is not a white-space, just move the cursor to the start.
                if (text[0].isWhitespace().not()) {
                    caret.removeSelection()
                    caret.moveToOffset(0)
                    editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
                // otherwise, move the caret to the first non-white space character
                } else {
                    goToComma(0, editor, caret)
                }
                return@forEach
            }

            goToComma(prevCommaIndex, editor, caret)
        }
    }
}


class GoToNextCommaWithSelection : GoToNextComma() {
    override fun actionPerformed(event: AnActionEvent) = performCommaWithSelectionAction(event, { super.actionPerformed(event) })
}

class GoToPrevCommaWithSelection : GoToPrevComma() {
    override fun actionPerformed(event: AnActionEvent) = performCommaWithSelectionAction(event, { super.actionPerformed(event) })
}

//region Helper Methods for Comma With Selection Actions
//TODO: This is not at a good state with detection of which text it should actually select - currently it tries to select all/most text [non-whitespace] between 2 commas, when the desired outcome should be to select the text that is surrounded by parenthesis of a method call (since this action should typically be used for selecting an argument of a method call)
private fun performCommaWithSelectionAction(
    event: AnActionEvent,
    goToComma: () -> Unit
) {
    val editor: Editor = event.getData(CommonDataKeys.EDITOR) ?: return
    val carets = editor.caretModel.allCarets
    val text = editor.document.charsSequence

    if (text.count { it == ',' } < 2)
        return

    for (caret in carets) {
        goToComma()

        caret.removeSelection()

        val leftComma = text.lastIndexOfOrNull(',',caret.offset) ?: 0
        val rightComma = text.indexOfOrNull(',',caret.offset) ?: text.lastIndex

        val (openerParenthesisIndex, closerParenthesisIndex) = findIndicesOfMethodCall(text, caret.offset)

        val startOfContent = (leftComma+1..rightComma).firstOrNull {
            text[it].isWhitespace().not()
        } ?: leftComma


        val stopCondition: (Int) -> Boolean = if (openerParenthesisIndex != null) {
            index -> index == closerParenthesisIndex
        } else {
            index -> text[index].isWhitespace().not()
        }

        val endOfContent = (startOfContent..rightComma - 1).firstOrNull {
            stopCondition(it)
        } ?: rightComma

        caret.setSelection(startOfContent, endOfContent)
    }
}

//endregion


/**
 * Moves the caret to the specified comma index.
 */
fun goToComma(commaIndex: Int, editor: Editor, caret: Caret) {
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
