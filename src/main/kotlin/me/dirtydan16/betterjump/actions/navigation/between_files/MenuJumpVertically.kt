package me.dirtydan16.betterjump.actions.navigation.between_files

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.components.JBList
import java.awt.Component
import java.awt.KeyboardFocusManager
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.SwingUtilities


abstract class AbstractNodeJumpAction(private val direction: Int) : AnAction(), DumbAware {
    override fun actionPerformed(event: AnActionEvent) {
        // try the focused component first
        val focusedComponent: Component? = KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner
        val component: Component? = findNavigableComponent(focusedComponent) ?: findInActivePopup()

        when (component) {
            is JTree -> performTreeJump(component)
            is JBList<*> -> performListJump(component)
        }
    }

    private fun findInActivePopup(): Component? {
        // get current focus owner as the root component
        val rootComponent: Component? = KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow
        if (rootComponent == null) return null


        val popupFactory: JBPopupFactory = JBPopupFactory.getInstance()
        val activePopups = popupFactory.getChildPopups(rootComponent)

        for (popup in activePopups) {
            try {
                val contentField = popup.javaClass.getDeclaredField("myContent")
                contentField.isAccessible = true
                val content = contentField.get(popup)
                if (content is JScrollPane) {
                    val view = content.viewport.view
                    if (view is JTree || view is JBList<*>) return view
                }
                if (content is JTree || content is JBList<*>) return content
            } catch (_: Exception) {
                continue
            }
        }
        return null
    }

    private fun findNavigableComponent(component: Component?): Component? {
        if (component == null) return null

        // Directly a tree or list
        if (component is JTree || component is JBList<*>) return component

        // Check scrollpane
        if (component is JScrollPane) {
            val view: Component? = component.viewport.view
            if (view is JTree || view is JBList<*>) return view
        }

        // Try parent chain
        val ancestorTree: Component? = SwingUtilities.getAncestorOfClass(JTree::class.java, component)
        if (ancestorTree != null) return ancestorTree

        val ancestorList: Component? = SwingUtilities.getAncestorOfClass(JBList::class.java, component)
        if (ancestorList != null) return ancestorList

        return null
    }

    private fun performTreeJump(tree: JTree) {
        val selRow: Int = tree.leadSelectionRow
        if (selRow == -1) return

        val rowCount: Int = tree.rowCount
        val jumpAmount = 4
        var targetRow: Int = selRow + direction * jumpAmount
        if (targetRow < 0) targetRow = 0
        if (targetRow >= rowCount) targetRow = rowCount - 1

        tree.setSelectionRow(targetRow)
        tree.scrollRowToVisible(targetRow)
    }

    private fun performListJump(list: JBList<*>) {
        val selIndex: Int = list.leadSelectionIndex
        if (selIndex == -1) return

        val size: Int = list.model.size
        val jumpAmount = 4
        var targetIndex: Int = selIndex + direction * jumpAmount
        if (targetIndex < 0) targetIndex = 0
        if (targetIndex >= size) targetIndex = size - 1

        list.setSelectedIndex(targetIndex)
        list.ensureIndexIsVisible(targetIndex)
    }
}


class NextNodeJumpDown : AbstractNodeJumpAction(1)

class NextNodeJumpUp : AbstractNodeJumpAction(-1)