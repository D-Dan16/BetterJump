package me.dirtydan16.betterjump.settings

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.dsl.builder.*
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent

class BetterJumpConfigurable : SearchableConfigurable {

    private lateinit var panel: DialogPanel

    override fun getId(): String = "me.dirtydan16.betterjump.settings"
    override fun getDisplayName(): String = "BetterJump"

    override fun createComponent(): JComponent {
        val settings = BetterJumpSettings.getInstance()
        panel = panel {
            group("Horizontal Jump") {
                row("Num of Words:") {
                    intTextField(range = 1..100)
                        .comment("How many editor words the caret moves when using [${shortcutTextFor("WordJumpToRight")}] and [${shortcutTextFor("WordJumpToLeft")}]")
                        .bindIntText(
                            getter = { settings.wordsToJumpHorizontally },
                            setter = { settings.wordsToJumpHorizontally = it.coerceAtLeast(1) }
                        )
                }

                row {
                    intTextField()
                        .label("Num of char:")
                        .comment(
                            "How many characters the caret moves when pressing [${shortcutTextFor("JumpHorizontal")}]"
                        )
                        .bindIntText(
                            getter = { settings.numOfCharactersToJumpToHorizontally },
                            setter = { settings.numOfCharactersToJumpToHorizontally = it }
                        )
                }

                row {
                    checkBox("Flip direction of Jump Horizontally action")
                        .comment("(num of chars to jump). To flip the direction type [${shortcutTextFor("InvertJumpHorizontalDirection")}]")
                        .bindSelected(
                            getter = { settings.isJumpHorizontallyFlipped },
                            setter = { settings.isJumpHorizontallyFlipped = it }
                        )
                }
            }
        }
        return panel
    }

    override fun isModified(): Boolean = panel.isModified()
    override fun apply() = panel.apply()
    override fun reset() = panel.reset()
}

private fun shortcutTextFor(actionId: String): String {
    val set = com.intellij.openapi.keymap.KeymapUtil.getActiveKeymapShortcuts(actionId)
    val shortcuts = set.shortcuts
    return if (shortcuts.isEmpty()) "no shortcut assigned"
    else com.intellij.openapi.keymap.KeymapUtil.getShortcutsText(shortcuts)
}
