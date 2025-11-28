package me.dirtydan16.betterjump.actions.navigation.inside_a_file

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.siblings
import fleet.util.indexOfOrNull
import org.jetbrains.kotlin.idea.base.codeInsight.handlers.fixers.range
import org.jetbrains.kotlin.idea.base.codeInsight.handlers.fixers.start
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid


sealed class GoToMethodCall() : AnAction() {
    @Suppress("UNCHECKED_CAST")
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val carets = editor.caretModel.allCarets
        val text = editor.document.charsSequence

        if (carets.isEmpty()) return

        //region Get Method Calls List
        val psiFile: PsiFile = e.dataContext.getData(CommonDataKeys.PSI_FILE) ?: return

        val codeFile: PsiClassOwner = when (psiFile) {
            is KtFile -> psiFile
            is PsiJavaFile -> psiFile
            else -> {
                println("The programming language in this file is not supported.")
                return
            }
        }

        val calls: MutableList<PsiElement> = mutableListOf()

        // add logic that collects each element that is recognized as a method call to the 'calls' list
        val fileVisitor = when (codeFile) {
            is KtFile -> {
                object : KtTreeVisitorVoid() {
                    override fun visitCallExpression(expression: KtCallExpression) {
                        calls.add(expression)
                        super.visitCallExpression(expression)
                    }
                }
            }
            is PsiJavaFile -> {
                object : JavaRecursiveElementVisitor() {
                    override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                        calls.add(expression)
                        super.visitMethodCallExpression(expression)
                    }
                }
            }
            else -> {
                println("The programming language in this file is not supported.")
                return
            }
        }

        // Actually visit the PSI tree
        when (codeFile) {
            is KtFile -> codeFile.accept(fileVisitor as KtTreeVisitorVoid)
            is PsiJavaFile -> codeFile.accept(fileVisitor as JavaRecursiveElementVisitor)
        }

        calls.sortBy { it.range.start }

        //endregion

        for (caret in carets) {
            //region Find Method caret is at
            var methodCallIndex: Int = 0

            for ((index, curMethodCall) in calls.withIndex()) {
                val range = curMethodCall.range
                when {
                    // Caret is inside this method call
                    range.contains(caret.offset) -> {
                        methodCallIndex = index
                        break
                    }
                    // Caret is before this method call
                    range.start > caret.offset -> {
                        // Edge case for if we are before the first method call
                        if (index == 0) {
                            if (this is JumpToNextMethodCall)
                                findOffsetAndJumpToIt(range,text,caret,editor)
                            break
                        }

                        methodCallIndex = index-1
                        break
                    }
                }
            }

            //endregion

            //region Find target method
            val targetMethodCall: TextRange? = when (this) {
                is JumpToNextMethodCall -> calls.elementAtOrNull(methodCallIndex+1)?.range
                is JumpToPrevMethodCall -> calls.elementAtOrNull(methodCallIndex-1)?.range
            }

            if (targetMethodCall == null) return
            //endregion

            //region Move to target method
            findOffsetAndJumpToIt(targetMethodCall, text, caret, editor)
            //endregion
        }

    }

    private fun findOffsetAndJumpToIt(
        targetMethodCall: TextRange,
        text: CharSequence,
        caret: Caret,
        editor: Editor,
    ) {
        // move to the first non-whitespace character in that found block if there is such
        var startOfParametersIndex: Int = (targetMethodCall.start..targetMethodCall.endOffset).firstOrNull {
            text[it] in listOf('(', '{')
        } ?: (targetMethodCall.start)

        startOfParametersIndex++

        val startOfContentIndex = (startOfParametersIndex..targetMethodCall.endOffset).firstOrNull {
            !(text[it].isWhitespace())
        } ?: (startOfParametersIndex)


        caret.moveToOffset(startOfContentIndex)
        editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
    }


}

class JumpToNextMethodCall : GoToMethodCall()

class JumpToPrevMethodCall : GoToMethodCall()


class TraverseThroughMethodCalls : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        TODO("Not yet implemented")
    }
}