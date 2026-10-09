package pro.artkart.algs.lafore.stack

import io.github.oshai.kotlinlogging.KotlinLogging

class Stack(
    private val capacity: Int
) {

    private val array = Array<Int>(capacity) { 0 }
    private var head = -1

    fun pop(): Int = when {
        isEmpty() -> throw StackIsEmpty()
        else -> array[head--]
    }

    fun push(item: Int) = when {
        isFull() -> throw StackIsFull()
        else -> array[++head] = item
    }

    fun pick(): Int = when {
        isEmpty() -> throw StackIsEmpty()
        else -> array[head]
    }

    fun isEmpty(): Boolean = head == -1

    fun isFull(): Boolean = head == capacity - 1
}

class StackIsEmpty : RuntimeException("Stack is empty")

class StackIsFull : RuntimeException("Stack is full")

fun main() {
    val log = KotlinLogging.logger { }
    val stack = Stack(10)
    stack.push(10)
    stack.push(20)
    stack.push(30)
    stack.push(40)
    while (!stack.isEmpty()) {
        log.info { stack.pop() }
    }
}
