package pro.artkart.algs.lafore.linked

class DoubleLinkedList<T>(
    var head: Node<T>? = null,
    var tail: Node<T>? = null
) {

    fun isEmpty(): Boolean = head == null

    fun insertFirst(item: T) = Node(item).let { node ->
        when {
            isEmpty() -> tail = node

            else -> {
                head?.prev = node
                node.next = head
            }
        }
        head = node
    }

    fun insertLast(item: T) = Node(item).let { node ->
        when {
            isEmpty() -> head = node

            else -> {
                tail?.next = node
                node.prev = tail
            }
        }
        tail = node
    }

    fun insertAfter(after: T, item: T) = Node(item).let { node ->
        var current = head
        while (current != null && current.value != after) {
            current = current.next
        }
        if (current == tail) {
            tail = node
        } else {
            node.next = current?.next
            current?.next?.prev = node
        }
        node.prev = current
        current?.next = node
    }

    fun removeFirst(): T? = head?.value
        ?.also {
            head?.next?.prev = null
            head = head?.next
        }

    fun removeLast(): T? = tail?.value
        ?.also {
            tail?.prev?.next = null
            tail = tail?.prev
        }

    fun remove(item: T): T? {
        var current = head
        while (current != null && current.value != item) {
            current = current.next
        }
        when (current) {
            head -> {
                current?.next?.prev = null
                head = current?.next
            }

            tail -> {
                current?.prev?.next = null
                tail = current?.prev
            }

            else -> {
                current?.prev?.next = current.next
                current?.next?.prev = current.prev
            }
        }
        return current?.value
    }

    fun findHeaded(item: T): Node<T>? {
        var current = head
        while (current != null && current.value != item) {
            current = current.next
        }
        return current
    }

    fun findTailed(item: T): Node<T>? {
        var current = tail
        while (current != null && current.value != item) {
            current = current.prev
        }
        return current
    }

    override fun toString(): String = "(DLL $head)"

    class Node<T>(
        val value: T,
        var next: Node<T>? = null,
        var prev: Node<T>? = null
    ) {
        override fun toString(): String = "(Node $value) <-> $next"
    }
}
