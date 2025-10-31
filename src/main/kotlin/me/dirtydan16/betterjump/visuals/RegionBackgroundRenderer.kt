package me.dirtydan16.betterjump.visuals

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.markup.LineMarkerRenderer
import com.intellij.openapi.util.TextRange
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle

internal class RegionBackgroundRenderer(
    private val editor: EditorEx,
    private val range: TextRange,
    private val color: Color
) : LineMarkerRenderer {
    override fun paint(editor: Editor, graphics: Graphics, r: Rectangle) {}
}
