package me.dirtydan16.betterjump.actions.navigation.inside_a_file

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import kotlin.collections.forEach


sealed class GoToMethodCall() : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val carets = editor.caretModel.allCarets

        if (carets.isEmpty()) return

        val methodCallPositions: List<OffsetRange> = getAllOffsetRangesOfMethodCallsInFile()


        carets.forEach { caret ->

            val (methodCallIndexCaretIsIn: Int,isBetweenMethods: Boolean) = run {
                val starts = 

                return@run methodCallPositions.lastIndex to true
            }

            val targetMethodCall: Int = if (!isBetweenMethods) {
                when (this) {
                    is JumpToNextMethodCall -> methodCallPositions[methodCallIndexCaretIsIn+1].start.coerceAtMost(methodCallPositions.lastIndex)
                    is JumpToPrevMethodCall -> methodCallPositions[methodCallIndexCaretIsIn-1].start.coerceAtLeast(0)
                }
            } else {
                when (this) {
                    is JumpToNextMethodCall -> methodCallPositions[methodCallIndexCaretIsIn+1].start.coerceAtMost(methodCallPositions.lastIndex)
                    is JumpToPrevMethodCall -> methodCallPositions[methodCallIndexCaretIsIn-1].start.coerceAtLeast(0)
                }
            }

            caret.moveToOffset(targetMethodCall)

        }

    }

    fun getAllOffsetRangesOfMethodCallsInFile(): List<OffsetRange> {
        return TODO("Provide the return value")
    }


}

data class OffsetRange(val start: Int, val end: Int)

class JumpToNextMethodCall : GoToMethodCall()

class JumpToPrevMethodCall : GoToMethodCall()


class TraverseThroughMethodCalls : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        TODO("Not yet implemented")
    }
}