package pro.artkart.algs.lafore.linked

class SortedLinkedList<T : Comparable<T>>(
    var head: Node<T>? = null
) {

    fun isEmpty(): Boolean = head == null

    fun insert(item: T) = Node(item).let {
        var current = head
        var previous: Node<T>? = null
        while (current != null && item > current.value) {
            previous = current
            current = current.next
        }
        it.next = current
        if (previous == null)
            head = it
        else
            previous.next = it
    }

    fun remove() = head?.value
        ?.also {
            head = head?.next
        }

    override fun toString(): String = "(Sorted $head)"

    class Node<T>(
        val value: T,
        var next: Node<T>? = null
    ) {
        override fun toString(): String = "(Node=$value) -> ${next.toString()}"
    }
}

fun <T : Comparable<T>> Array<T>.sortIt() =
    SortedLinkedList<T>().let { sorted ->
        forEach(sorted::insert)
        indices.forEach {
            this[it] = sorted.remove()!!
        }
    }
