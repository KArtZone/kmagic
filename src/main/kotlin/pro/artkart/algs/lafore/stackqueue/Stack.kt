package pro.artkart.algs.lafore.stackqueue

class Stack(
    private val capacity: Int
) {

    private val stack = Array(capacity) { 0 }

    private var head = -1

    fun push(item: Int) = when {
        isFull() -> throw RuntimeException("Stack is full")
        else -> stack[++head] = item
    }

    fun pop(): Int = when {
        isEmpty() -> throw RuntimeException("Stack is empty")
        else -> stack[head--]
    }

    fun peak(): Int = when {
        isEmpty() -> throw RuntimeException("Stack is empty")
        else -> stack[head]
    }

    fun isEmpty(): Boolean = head == -1

    fun isFull(): Boolean = head == capacity - 1
}

fun String.reverse(): String = Stack(length).let { stack ->
    forEach { char -> stack.push(char.code) }
    buildString {
        while (!stack.isEmpty()) {
            append(stack.pop().toChar())
        }
    }
}

fun String.checkBrackets(): String {
    fun checkPair(bracketPair: Pair<Char, Char>): Boolean = when (bracketPair) {
        '{' to '}', '[' to ']', '(' to ')' -> true
        else -> false
    }

    val stack = Stack(length)
    forEachIndexed { index, char ->
        when (char) {
            '{', '[', '(' -> stack.push(char.code)
            '}', ']', ')' -> {
                if (!checkPair(Pair(stack.pop().toChar(), char))) {
                    return "Error: $char at $index"
                }
            }
        }
    }
    return "Ok"
}
