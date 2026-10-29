package pro.artkart.algs.lafore.linked.stackqueue

import pro.artkart.algs.lafore.linked.HeadedLinkedList

class Stack<T>(
    private val stack: HeadedLinkedList<T> = HeadedLinkedList()
) {

    fun push(item: T) = stack.insertFirst(item)

    fun pop(): T? = stack.deleteFirst()

    fun isEmpty(): Boolean = stack.isEmpty()

    override fun toString(): String = "(Stack $stack)"
}
