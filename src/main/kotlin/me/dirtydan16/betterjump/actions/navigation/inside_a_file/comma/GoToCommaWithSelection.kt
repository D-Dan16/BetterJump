package me.dirtydan16.betterjump.actions.navigation.inside_a_file.comma

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import me.dirtydan16.betterjump.utils.additions.findIndicesOfMethodCall
import me.dirtydan16.betterjump.utils.extensions.indexOfOrNull
import me.dirtydan16.betterjump.utils.extensions.lastIndexOfOrNull

class GoToNextCommaWithSelection : AnAction() {
    override fun actionPerformed(event: AnActionEvent) = performCommaWithSelectionAction(event, { goToNextComma(event) })
}

class GoToPrevCommaWithSelection : AnAction() {
    override fun actionPerformed(event: AnActionEvent) = performCommaWithSelectionAction(event, {goToPrevComma(event)})
}

//TODO: This is not at a good state with detection of which text it should actually select - currently it tries to select all/most text [non-whitespace] between 2 commas, when the desired outcome should be to select the text that is surrounded by parenthesis of a method call (since this action should typically be used for selecting an argument of a method call)
fun performCommaWithSelectionAction(
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