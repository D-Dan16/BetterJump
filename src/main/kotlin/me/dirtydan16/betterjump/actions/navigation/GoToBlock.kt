package me.dirtydan16.betterjump.actions.navigation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import me.dirtydan16.betterjump.utils.additions.findIndicesOfCodeBlock

class GoToNextBlock : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val carets = editor.caretModel.allCarets
        val text = editor.document.charsSequence

        for (caret in carets) {
            // We want to skip the current Block we are in, so we don't stumble upon our braces. So we start the search *outside* of them if we are inside braces.
            val startSearchIndex: Int = findIndicesOfCodeBlock(text,caret.offset).second ?: caret.offset
            val endSearchIndex: Int = findIndicesOfCodeBlock(text,startSearchIndex).second ?: caret.offset

            // If we are inside a block, we want to start searching for the next block after the end of the current block. If there is no end, stop and move to the next caret
            val openingBraceIndex = (startSearchIndex..endSearchIndex).firstOrNull {
                text[it] == '{'
            } ?: continue


            // if we have found a valid block to jump to, let's find the space it occupies
            val rangeOfIndicesOfNextBlock: IntRange = findIndicesOfCodeBlock(text, openingBraceIndex+1).run {
                first!!+1..second!!-1
            }

            // move to the first non-whitespace character in that found block if there is such
            val startOfContentIndex =  rangeOfIndicesOfNextBlock.firstOrNull {
                !(text[it].isWhitespace())
            } ?: (rangeOfIndicesOfNextBlock.first)

            caret.moveToOffset(startOfContentIndex)
            editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        }
    }
}

class GoToPreviousBlock : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val carets = editor.caretModel.allCarets
        val text = editor.document.charsSequence

        for (caret in carets) {
            // We want to skip the current Block we are in, so we don't stumble upon our braces. So we start the search *outside* of them if we are inside braces.
            val startSearchIndex: Int = findIndicesOfCodeBlock(text, caret.offset).first ?: caret.offset
            val endSearchIndex: Int = findIndicesOfCodeBlock(text,startSearchIndex).first ?: caret.offset

            // If we are inside a block, we want to start searching for the next block after the end of the current block. If there is no end, stop and move to the next caret
            val closingBraceIndex = (startSearchIndex downTo endSearchIndex).firstOrNull {
                text[it] == '}'
            } ?: continue

            val rangeOfIndicesOfPrevBlock: IntRange = findIndicesOfCodeBlock(text, closingBraceIndex-1).run {
                first!!+1..second!!-1
            }

            val startOfContentIndex = rangeOfIndicesOfPrevBlock.firstOrNull {
                !(text[it].isWhitespace())
            } ?: (rangeOfIndicesOfPrevBlock.first)

            caret.moveToOffset(startOfContentIndex)
            editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        }
    }
}

class GoToInnerBlock : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        TODO("Not yet implemented")
    }
}

class GoToOuterBlock : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        TODO("Not yet implemented")
    }
}


