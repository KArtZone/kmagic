package pro.artkart.algs.lafore.linked

class LinkedList<T>(
    var head: Node<T>? = null
) {

    fun isEmpty(): Boolean = head == null

    fun insertFirst(item: T) {
        head = Node(item, head)
    }

    fun deleteFirst() = head?.value
        .also {
            if (!isEmpty()) {
                head = head?.next
            }
        }

    fun find(item: T): Node<T>? {
        var current = head
        while (current != null && current.value != item)
            current = current.next
        return current
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

    override fun toString(): String = "(LL $head)"
}
