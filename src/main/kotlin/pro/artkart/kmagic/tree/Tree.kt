package pro.artkart.kmagic.tree

import pro.artkart.kmagic.list.ImmutableList

sealed class Tree<out T : Comparable<@UnsafeVariance T>> {

    internal object Empty : Tree<Nothing>() {
        override fun isEmpty(): Boolean = true
        override fun toString(): String = "E"
    }

    internal data class Node<out T : Comparable<@UnsafeVariance T>>(
        val left: Tree<T>,
        val value: T,
        val right: Tree<T>
    ) : Tree<T>() {
        override fun isEmpty(): Boolean = false

        override fun toString(): String = "(Node $left $value $right)"
    }

    abstract fun isEmpty(): Boolean

    operator fun plus(element: @UnsafeVariance T): Tree<T> = when (this) {
        Empty -> Node(Empty, element, Empty)

        is Node -> when {
            element < value -> Node(left + element, value, right)
            element > value -> Node(left, value, right + element)
            else -> Node(left, element, right)
        }
    }

    fun contains(item: @UnsafeVariance T): Boolean = when (this) {
        Empty -> false
        is Node -> when {
            item < value -> left.contains(item)
            item > value -> right.contains(item)
            else -> value == item
        }
    }

    companion object {

        operator fun <T : Comparable<T>> invoke(): Tree<T> = Empty

        operator fun <T : Comparable<T>> invoke(vararg items: T): Tree<T> =
            items.fold(invoke()) { acc, item ->
                acc + item
            }

        operator fun <T : Comparable<T>> invoke(list: ImmutableList<T>): Tree<T> =
            list.foldLeft(invoke()) { acc -> { item -> acc + item } }
    }
}

fun <T : Comparable<T>> ImmutableList<T>.toTree(): Tree<T> =
    foldLeft(Tree()) { acc ->
        { item -> acc + item }
    }
