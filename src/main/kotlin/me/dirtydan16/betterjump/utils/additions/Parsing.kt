package me.dirtydan16.betterjump.utils.additions

//@CheckReturnValue
/**
 * Identifies the indices of the opening and closing parentheses
 * for the method call where the caret is currently positioned.
 *
 * @param text The text content in which the method call search is performed.
 * @param caretPosition The current position of the caret in the text.
 * @return A pair of indices representing the opening and closing parentheses
 *         of the method call, or a pair of null values if no such match is found.
 */
fun findIndicesOfMethodCall(text: CharSequence, caretPosition: Int): Pair<Int?, Int?> {
    return findIndicesOfSurroundingBlock(text, caretPosition, '(', ')')
}

fun findIndicesOfBrackets(text: CharSequence, caretPosition: Int): Pair<Int?, Int?> {
    return findIndicesOfSurroundingBlock(text, caretPosition, '[', ']')
}

fun findIndicesOfCodeBlock(text: CharSequence, caretPosition: Int): Pair<Int?, Int?> {
    return findIndicesOfSurroundingBlock(text, caretPosition, '{', '}')
}

private fun findIndicesOfSurroundingBlock(
    text: CharSequence,
    caretPosition: Int,
    openerChar: Char,
    closerChar: Char,
): Pair<Int?, Int?> {
    val stack = ArrayDeque<Int>()   // stores indices of the opener character
    val pairs: MutableList<Pair<Int, Int>> = mutableListOf()  // stores matched pairs

    // Step 1: match all openers, with closers

    var shouldIgnoreChar = text[0] == '"'
    text.forEachIndexed { index, ch ->
        if (shouldIgnoreChar) {
            if (ch == '"')
                shouldIgnoreChar = false
        } else {
            when (ch) {
                '"' -> shouldIgnoreChar = true
                openerChar -> stack.addLast(index)
                closerChar -> if (stack.isNotEmpty()) {
                    pairs += stack.removeLast() to index
                }
            }
        }
    }

    // Step 2: find the pair that surrounds the caret. We take the last pair since it is the most inner pair.
    val match = pairs.lastOrNull { (open, close) ->
        caretPosition in (open + 1)..(close - 1)
    }

    return match ?: (null to null)
}