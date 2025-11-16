package me.dirtydan16.betterjump.actions.navigation.inside_a_file

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.base.codeInsight.handlers.fixers.range
import org.jetbrains.kotlin.idea.base.codeInsight.handlers.fixers.start
import kotlin.collections.forEach
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid


sealed class GoToMethodCall() : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val carets = editor.caretModel.allCarets
        val text = editor.document.charsSequence

        if (carets.isEmpty()) return

        //region Get Method Calls List
        val psiFile: PsiFile = e.dataContext.getData(CommonDataKeys.PSI_FILE) ?: return
        val ktFile = psiFile as? KtFile ?: run {
            println("problem in finding the kotlin file"); return
        }
        val calls: MutableList<KtCallExpression> = mutableListOf()
        ktFile.accept(object : KtTreeVisitorVoid() {
            override fun visitCallExpression(expression: KtCallExpression) {
                calls.add(expression)
                super.visitCallExpression(expression)
            }
        })
        val methodCallPositions: List<TextRange> = calls.map {
            it.range
        }
        //endregion

        carets.forEach { caret ->

            var methodCallIndexCaretIsIn: Int = methodCallPositions.lastIndex
            var isBetweenMethods = false

            for (i in methodCallPositions.indices) {
                val position = methodCallPositions[i]

                when {
                    // Caret is inside this method call
                    position.contains(caret.offset) -> {
                        val nextPosition = methodCallPositions.getOrNull(i + 1)
                        // we check if the caret isn't inside multiple method calls. if it is, we will skip until we reach the last method call the caret is at, which *should* be the innermost method call.
                        if (nextPosition == null || !nextPosition.contains(caret.offset)) {
                            methodCallIndexCaretIsIn = i
                            break
                        }
                    }
                    // Caret is before this method call
                    position.start > caret.offset -> {
                        methodCallIndexCaretIsIn = i - 1
                        isBetweenMethods = true
                        break
                    }
                }
            }

            fun topCap(index: Int): Int = index.coerceAtMost(methodCallPositions.lastIndex)
            fun bottomCap(index: Int): Int = index.coerceAtLeast(0)

            val targetMethodCall: TextRange = when (this) {
                is JumpToNextMethodCall -> methodCallPositions[topCap(methodCallIndexCaretIsIn+1)]
                is JumpToPrevMethodCall -> {
                    if (!isBetweenMethods)
                        methodCallPositions[bottomCap(methodCallIndexCaretIsIn - 1)]
                    else
                        methodCallPositions[bottomCap(methodCallIndexCaretIsIn)]
                }
            }

            // move to the first non-whitespace character in that found block if there is such
            var startOfParametersIndex: Int = (targetMethodCall.start..targetMethodCall.endOffset).firstOrNull {
                text[it] in listOf('(','{')
            } ?: (targetMethodCall.start)

            startOfParametersIndex++

            val startOfContentIndex = (startOfParametersIndex..targetMethodCall.endOffset).firstOrNull {
                !(text[it].isWhitespace())
            } ?: (startOfParametersIndex)


            caret.moveToOffset(startOfContentIndex)
            editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        }

    }


}

class JumpToNextMethodCall : GoToMethodCall()

class JumpToPrevMethodCall : GoToMethodCall()


class TraverseThroughMethodCalls : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        TODO("Not yet implemented")
    }
}