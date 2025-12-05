package me.dirtydan16.betterjump.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

data class State(
    var wordsToJumpHorizontally: Int = 3,
    var numOfCharactersToJumpToHorizontally: Int = 20,
    var isJumpHorizontallyFlipped: Boolean = false,
    var menuJumpAmountVertically: Int = 3
)

@Service(Service.Level.APP)
@State(name = "BetterJumpSettings", storages = [Storage("betterJump.xml")])
class BetterJumpSettings : PersistentStateComponent<me.dirtydan16.betterjump.settings.State> {
    private var state = State()

    var wordsToJumpHorizontally: Int
        get() = state.wordsToJumpHorizontally
        set(value) {
            state.wordsToJumpHorizontally = value.coerceAtLeast(1)
        }

    var numOfCharactersToJumpToHorizontally: Int
        get() = state.numOfCharactersToJumpToHorizontally
        set(value) {
            state.numOfCharactersToJumpToHorizontally = value
        }

    var isJumpHorizontallyFlipped: Boolean
        get() = state.isJumpHorizontallyFlipped
        set(value) {
            state.isJumpHorizontallyFlipped = value
        }

    var menuJumpAmountVertically: Int
        get() = state.menuJumpAmountVertically
        set(value) {
            state.menuJumpAmountVertically = value
        }



    override fun getState(): me.dirtydan16.betterjump.settings.State = state

    override fun loadState(_state: me.dirtydan16.betterjump.settings.State) {
        state = _state
        state.wordsToJumpHorizontally = state.wordsToJumpHorizontally.coerceAtLeast(1)
        state.numOfCharactersToJumpToHorizontally = state.numOfCharactersToJumpToHorizontally
        state.isJumpHorizontallyFlipped = state.isJumpHorizontallyFlipped
    }

    companion object {
        fun getInstance(): BetterJumpSettings = service()
    }
}
