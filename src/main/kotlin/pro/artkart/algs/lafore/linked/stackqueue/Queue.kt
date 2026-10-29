package pro.artkart.algs.lafore.linked.stackqueue

import pro.artkart.algs.lafore.linked.HeadedTailedLinkedList

class Queue<T>(
    private val queue: HeadedTailedLinkedList<T> = HeadedTailedLinkedList()
) {

    fun insert(item: T) = queue.insertLast(item)

    fun remove() = queue.deleteFirst()

    fun isEmpty() = queue.isEmpty()

    override fun toString(): String = "(Queue $queue)"
}
