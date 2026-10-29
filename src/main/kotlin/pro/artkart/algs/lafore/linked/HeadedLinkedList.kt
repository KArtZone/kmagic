package pro.artkart.algs.lafore.linked

class HeadedLinkedList<T>(
    var head: Node<T>? = null
) {

    fun isEmpty(): Boolean = head == null

    fun insertFirst(item: T) {
        head = Node(item, head)
    }

    fun insertLast(item: T) {
        if (isEmpty())
            head = Node(item)
        else {
            var current = head
            while (current?.next != null) {
                current = current.next
            }
            current?.next = Node(item)
        }
    }

    fun deleteFirst() = head?.value
        .also {
            if (!isEmpty()) {
                head = head?.next
            }
        }

    fun delete(item: T): Node<T>? {
        var current = head
        var previous = head
        while (current != null && current.value != item) {
            previous = current
            current = current.next
        }
        return current?.also {
            if (it == head)
                head = head?.next
            else
                previous?.next = it.next
        }
    }

    fun find(item: T): Node<T>? {
        var current = head
        while (current?.next != null && current.value != item)
            current = current.next
        return if (current?.next == null) null else current
    }

    fun iterator() = Iterator()

    override fun toString(): String = "(LL $head)"

    inner class Iterator(
        private var current: Node<T>? = head,
        private var prev: Node<T>? = null
    ) {

        fun reset() {
            current = head
            prev = null
        }

        fun next() {
            prev = current
            current = current?.next
        }

        fun current() = current?.value

        fun hasNext() = current?.next != null

        fun insertAfter(item: T) = Node(item).let { node ->
            when {
                isEmpty() -> {
                    head = node
                    current = node
                }

                else -> {
                    node.next = current?.next
                    current?.next = node
                    next()
                }
            }
        }

        fun insertBefore(item: T) = Node(item).let { node ->
            when (prev) {
                null -> {
                    node.next = head
                    head = node
                    reset()
                }

                else -> {
                    node.next = prev?.next
                    prev?.next = node
                    current = node
                }
            }
        }

        fun delete() = current?.value
            ?.also {
                when (prev) {
                    null -> {
                        head = current?.next
                        reset()
                    }

                    else -> {
                        prev?.next = current?.next
                        if (hasNext())
                            current = current?.next
                        else
                            reset()
                    }
                }
            }
    }

    class Node<T>(
        val value: T,
        var next: Node<T>? = null
    ) {
        override fun toString(): String = "(Node=$value) -> ${next.toString()}"
    }
}
