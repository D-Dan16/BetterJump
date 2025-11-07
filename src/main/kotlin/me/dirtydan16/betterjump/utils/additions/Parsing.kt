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

/**
 * Identifies the indices of the opening and closing curly braces.
 *
 * @return a pair of indexes - the index of the opener is the index of '{', and the closer the index of the '}'. returns Null if there is no code block surrounding the given position
 */
fun findIndicesOfCodeBlock(text: CharSequence, caretPosition: Int): Pair<Int?, Int?> {
    return findIndicesOfSurroundingBlock(text, caretPosition, '{', '}')
}

fun findRangeOfCodeBlock(text: CharSequence, caretPosition: Int): IntRange? {
    return findIndicesOfSurroundingBlock(text, caretPosition, '{', '}').run {
        val (start, end) = this
        if (start != null && end != null) start+1..end-1 else null
    }
}



private fun findIndicesOfSurroundingBlock(
    text: CharSequence,
    caretPosition: Int,
    openerChar: Char,
    closerChar: Char,
): Pair<Int?, Int?> {
    if (text.isEmpty())
        return null to null

    val stack: ArrayDeque<Int> = ArrayDeque()   // stores indices of the opener character
    val pairs: MutableList<Pair<Int, Int>> = mutableListOf()  // stores matched pairs

    // Step 1: match all openers, with closers

    val insideStringChars = listOf('"', '\'')
    var parsingState = ParsingStates.REGULAR

    for ((index, ch) in text.withIndex()) {
        when (parsingState){
            ParsingStates.IN_TEXT -> {
                if (ch in insideStringChars)
                    parsingState = ParsingStates.REGULAR
            }
            ParsingStates.REGULAR -> {
                when (ch) {
                    in insideStringChars -> {
                        parsingState = ParsingStates.IN_TEXT
                    }
                    '/' -> {
                        when (text[index+1]) {
                            '/' -> parsingState = ParsingStates.IN_COMMENT
                            '*' -> parsingState = ParsingStates.IN_MULTI_LINE_COMMENT
                        }
                    }
                    openerChar -> stack.addLast(index)
                    closerChar -> if (stack.isNotEmpty()) {
                        pairs += stack.removeLast() to index
                    }
                }
            }
            ParsingStates.IN_COMMENT -> {
                if (ch == '\n') {
                    parsingState = ParsingStates.REGULAR
                }
            }

            ParsingStates.IN_MULTI_LINE_COMMENT -> {
                if (text[index] == '*' && text[index+1] == '/') {
                    parsingState = ParsingStates.REGULAR
                }
            }
        }
    }

    // Step 2: find the pair that surrounds the caret. We take the first pair since it is the most inner pair.
    val match = pairs.firstOrNull { (open, close) ->
        caretPosition in (open + 1)..(close - 1)
    }

    return match ?: (null to null)
}

enum class ParsingStates {
    REGULAR,IN_TEXT,IN_COMMENT,IN_MULTI_LINE_COMMENT
}