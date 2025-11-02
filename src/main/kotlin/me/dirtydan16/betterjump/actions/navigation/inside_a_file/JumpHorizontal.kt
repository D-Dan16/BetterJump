package me.dirtydan16.betterjump.actions.navigation.inside_a_file

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.ui.Messages
import me.dirtydan16.betterjump.settings.BetterJumpSettings
import kotlin.math.abs

class SetAndJumpHorizontal : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor = e.getData(CommonDataKeys.EDITOR) ?: return

        val jumpSettings = BetterJumpSettings.getInstance()

        val jumpAmount: Int = Messages.showInputDialog(
            e.project,
            "Jump Horizontally By the specified amount of characters.",
            "Better Jump",
            Messages.getQuestionIcon(),
            jumpSettings.numOfCharactersToJumpToHorizontally.toString(),
            null
        )?.toInt() ?: return

        jumpSettings.numOfCharactersToJumpToHorizontally = jumpAmount

        val direction = if (BetterJumpSettings.getInstance().isJumpHorizontallyFlipped) -1 else 1

        editor.caretModel.allCarets.forEach {
            it.moveToOffset(it.offset+jumpAmount*direction)
            editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        }
    }
}

class InvertJumpHorizontalDirection : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val jumpSettings = BetterJumpSettings.getInstance()
        jumpSettings.isJumpHorizontallyFlipped = jumpSettings.isJumpHorizontallyFlipped.not()
    }
}

class JumpHorizontal : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return

        val jumpAmount = BetterJumpSettings.getInstance().numOfCharactersToJumpToHorizontally
        val direction = if (BetterJumpSettings.getInstance().isJumpHorizontallyFlipped) -1 else 1

        editor.caretModel.allCarets.forEach {
            it.moveToOffset(it.offset+jumpAmount*direction)
            editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        }
    }

}


class JumpToRight : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor: Editor = event.getData(CommonDataKeys.EDITOR) ?: return
        editor.caretModel.allCarets.forEach {
            moveCaretHorizontally(
                editor, it, BetterJumpSettings.getInstance().wordsToJumpHorizontally
            )
        }
    }
}

class JumpToLeft : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor: Editor = event.getData(CommonDataKeys.EDITOR) ?: return
        editor.caretModel.allCarets.forEach {
            moveCaretHorizontally(
                editor, it, -BetterJumpSettings.getInstance().wordsToJumpHorizontally
            )
        }
    }
}

private fun moveCaretHorizontally(
    editor: Editor,
    caret: Caret,
    numOfWordsToJump: Int
) {
    if (numOfWordsToJump == 0)
        return

    val direction = if (numOfWordsToJump > 0) {
        IdeActions.ACTION_EDITOR_NEXT_WORD
    } else {
        IdeActions.ACTION_EDITOR_PREVIOUS_WORD
    }

    repeat(abs(numOfWordsToJump)) {
        val handler = EditorActionManager.getInstance().getActionHandler(direction)
        val dataContext = DataManager.getInstance().getDataContext(editor.contentComponent)
        handler.execute(editor, caret, dataContext)
    }
}
