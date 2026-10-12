package pro.artkart.algs.lafore.stackqueue

import pro.artkart.kmagic.common.Option
import pro.artkart.kmagic.common.Option.None

class Stack<T> {

    var head: Option<OneWayNode<T>> = None
    var size = 0

    fun push(item: T) {
        head = Option(OneWayNode(item, head))
        ++size
    }

    fun pop(): Option<T> = head.map {
        head = it.next
        --size
        it.value
    }

    fun peek(): Option<T> = head.map { it.value }

    fun isEmpty(): Boolean = size == 0

    override fun toString(): String = "Stack(${head.map { "$it" }.getOrElse { EMPTY }})"
}
