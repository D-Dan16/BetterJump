package me.dirtydan16.betterjump.actions.comma_navigation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import me.dirtydan16.betterjump.utils.additions.findIndicesOfMethodCall

open class GoToNextComma : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor: Editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val carets = editor.caretModel.allCarets
        val text = editor.document.charsSequence

        carets.forEach { char ->
            val nextCommaIndex = text.indexOf(',', char.offset)

            // If there is no comma to jump to, just loop to the very start of the file
            if (nextCommaIndex == -1) {
                char.moveToOffset(text.indexOfFirst { it.isWhitespace().not() })
                editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
            } else {
                goToComma(nextCommaIndex, editor, char)
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
            val curCommaIndex = (caret.offset - 1 downTo 0).firstOrNull { i -> text[i] == ',' } ?: -1
            if (curCommaIndex == -1) {
                val lastCommaIndex = text.indexOfLast { it == ',' }
                val firstNonWhitespacePos = (lastCommaIndex..text.lastIndex).first { text[it].isWhitespace().not() }
                caret.moveToOffset(firstNonWhitespacePos)
                editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
                return
            }

            // From that position, find the *previous* comma before it
            val prevCommaIndex = (curCommaIndex - 1 downTo 0).firstOrNull { i -> text[i] == ',' } ?: -1

            // If there is no comma to jump to, jump to the first non-white-space character from the start of the file
            if (prevCommaIndex == -1) {
                //edge case: if the very first character is not a white-space, just move the cursor to the start.
                if (text[0].isWhitespace().not()) {
                    caret.removeSelection()
                    caret.moveToOffset(0)
                    editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
                // otherwise, move the caret to the first non-white space character
                } else {
                    goToComma(0, editor, caret)
                }

            } else {
                goToComma(prevCommaIndex, editor, caret)
            }
        }
    }
}


class GoToNextCommaWithSelection : GoToNextComma() {
    override fun actionPerformed(event: AnActionEvent) = performCommaWithSelectionAction(event, true,{ super.actionPerformed(event) })
}

class GoToPrevCommaWithSelection : GoToPrevComma() {
    override fun actionPerformed(event: AnActionEvent) = performCommaWithSelectionAction(event, false,{ super.actionPerformed(event) })
}

//region Helper Methods for Comma With Selection Actions
//TODO: This is not at a good state with detection of which text it should actually select - currently it tries to select all/most text [non-whitespace] between 2 commas, when the desired outcome should be to select the text that is surrounded by parenthesis of a method call (since this action should typically be used for selecting an argument of a method call)
private fun performCommaWithSelectionAction(
    event: AnActionEvent,
    movesForwards: Boolean,
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

        val leftComma = (caret.offset downTo 0).firstOrNull { text[it] == ',' } ?: 0
        val rightComma = (caret.offset+1..text.lastIndex).firstOrNull { text[it] == ',' } ?: text.lastIndex

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
