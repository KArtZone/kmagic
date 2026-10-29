package pro.artkart.algs.lafore.linked

class HeadedTailedLinkedList<T>(
    var head: Node<T>? = null,
    var tail: Node<T>? = null,
) {

    fun isEmpty(): Boolean = head == null

    fun insertFirst(item: T) =
        Node(item).let {
            when {
                isEmpty() -> tail = it
                else -> it.next = head
            }
            head = it
        }

    fun insertLast(item: T) = Node(item).let {
        when {
            isEmpty() -> head = it
            else -> tail?.next = it
        }
        tail = it
    }

    fun deleteFirst(): T? = head?.value
        ?.also {
            head = head?.next
            if (head == null)
                tail = null
        }

    override fun toString(): String = "(HTList $head)"

    class Node<T>(
        val value: T,
        var next: Node<T>? = null
    ) {
        override fun toString(): String = "(Node=$value) -> ${next.toString()}"
    }
}
