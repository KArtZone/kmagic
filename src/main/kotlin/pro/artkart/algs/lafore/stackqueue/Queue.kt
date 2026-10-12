package pro.artkart.algs.lafore.stackqueue

import pro.artkart.kmagic.common.Option
import pro.artkart.kmagic.common.Option.None

class Queue<T> {

    var head: Option<OneWayNode<T>> = None
    var tail: Option<OneWayNode<T>> = None
    var size = 0

    fun insert(item: T) {
        OneWayNode(item).let { newNode ->
            when {
                isEmpty() -> {
                    head = Option(newNode)
                    tail = Option(newNode)
                }

                else -> {
                    tail.map { tailNode ->
                        tailNode.next = Option(newNode)
                    }
                    tail = Option(newNode)
                }
            }
            ++size
        }
    }

    fun remove(): Option<T> = head.map {
        head = it.next
        --size
        if (isEmpty())
            tail = None
        it.value
    }

    fun peek(): Option<T> = head.map { it.value }

    fun isEmpty(): Boolean = size == 0

    fun size() = size

    override fun toString(): String = "Queue(${
        head.map { "$it" }.getOrElse { EMPTY }
    })"
}
