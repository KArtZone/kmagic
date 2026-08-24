package pro.artkart.kmagic.tree

sealed class Tree<out T : Comparable<@UnsafeVariance T>> {

    abstract fun isEmpty(): Boolean

    operator fun plus(element: @UnsafeVariance T): Tree<T> = when (this) {
        Empty -> Node(Empty, element, Empty)

        is Node -> when {
            element < value -> Node(left + element, value, right)
            element > value -> Node(left, value, right + element)
            else -> Node(left, element, right)
        }
    }

    internal object Empty : Tree<Nothing>() {
        override fun isEmpty(): Boolean = true
        override fun toString(): String = "E"
    }

    internal class Node<out T : Comparable<@UnsafeVariance T>>(
        val left: Tree<T>,
        val value: T,
        val right: Tree<T>
    ) : Tree<T>() {
        override fun isEmpty(): Boolean = false

        override fun toString(): String = "(Node $left $value $right)"
    }

    companion object {

        operator fun <T : Comparable<T>> invoke(): Tree<T> = Empty
    }
}
