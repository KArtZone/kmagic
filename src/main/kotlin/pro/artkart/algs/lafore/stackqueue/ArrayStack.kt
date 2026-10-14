package pro.artkart.algs.lafore.stackqueue

class ArrayStack(
    private val capacity: Int
) {

    private val stack = IntArray(capacity)

    private var head = -1

    fun push(item: Int) {
        check(!isFull()) { "Stack is full" }
        stack[++head] = item
    }

    fun pop(): Int {
        check(!isEmpty()) { "Stack is empty" }
        return stack[head--]
    }

    fun peek(): Int {
        check(!isEmpty()) { "Stack is empty" }
        return stack[head]
    }

    fun isEmpty(): Boolean = head == -1

    fun isFull(): Boolean = head == capacity - 1
}

fun String.reverse(): String = ArrayStack(length).let { stack ->
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

    val stack = ArrayStack(length)
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
