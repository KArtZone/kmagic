package pro.artkart.algs.lafore.stackqueue

import pro.artkart.kmagic.common.Option
import pro.artkart.kmagic.common.Option.None

class Deque<T> {

    var size = 0
    var left: Option<Node<T>> = None
    var right: Option<Node<T>> = None

    fun insertLeft(item: T) = Node(item)
        .let { newNode ->
            when {
                isEmpty() -> {
                    left = Option(newNode)
                    right = Option(newNode)
                }

                else -> {
                    newNode.next = left
                    left.map { it.previous = Option(newNode) }
                    left = Option(newNode)
                }
            }
            ++size
        }


    fun insertRight(item: T) = Node(item)
        .let { newNode ->
            when {
                isEmpty() -> {
                    left = Option(newNode)
                    right = Option(newNode)
                }

                else -> {
                    newNode.previous = right
                    right.map { it.next = Option(newNode) }
                    right = Option(newNode)
                }
            }
            ++size
        }

    fun removeLeft(): Option<T> = left.map { leftNode ->
        leftNode.next.map { it.previous = None }
        left = leftNode.next
        --size
        if (isEmpty())
            right = None
        leftNode.value
    }

    fun removeRight(): Option<T> = right.map { rightNode ->
        rightNode.previous.map { it.next = None }
        right = rightNode.previous
        --size
        if (isEmpty())
            left = None
        rightNode.value
    }

    fun peekLeft(): Option<T> = left.map { it.value }

    fun peekRight(): Option<T> = right.map { it.value }

    fun isEmpty(): Boolean = size == 0

    override fun toString(): String = "Deque(${
        left.map { "$it" }.getOrElse { EMPTY }
    })"

    class Node<T>(
        val value: T,
        var next: Option<Node<T>> = None,
        var previous: Option<Node<T>> = None
    ) {

        override fun toString(): String = "$value -> ${
            next.map { "$it" }.getOrElse { EMPTY }
        }"
    }
}
