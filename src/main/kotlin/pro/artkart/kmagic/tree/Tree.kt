package pro.artkart.kmagic.tree

import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.list.ImmutableList
import kotlin.math.max

sealed class Tree<out T : Comparable<@UnsafeVariance T>> {

    abstract val size: Int

    abstract val height: Int

    abstract fun isEmpty(): Boolean

    internal object Empty : Tree<Nothing>() {
        override val size: Int = 0
        override val height: Int = -1
        override fun isEmpty(): Boolean = true
        override fun toString(): String = "E"
    }

    internal data class Node<out T : Comparable<@UnsafeVariance T>>(
        val left: Tree<T>,
        val value: T,
        val right: Tree<T>
    ) : Tree<T>() {
        override val size: Int = 1 + left.size + right.size
        override val height: Int = 1 + max(left.height, right.height)
        override fun isEmpty(): Boolean = false
        override fun toString(): String = "(Node $left $value $right)"
    }

    fun contains(item: @UnsafeVariance T): Boolean = when (this) {
        Empty -> false
        is Node -> when {
            item < value -> left.contains(item)
            item > value -> right.contains(item)
            else -> value == item
        }
    }

    fun max(): Resolution<T> = when (this) {
        Empty -> Resolution.Empty
        is Node -> right.max().orElse { Resolution(value) }
    }

    fun min(): Resolution<T> = when (this) {
        Empty -> Resolution.Empty
        is Node -> left.min().orElse { Resolution(value) }
    }

    fun remove(item: @UnsafeVariance T): Tree<T> = when (this) {
        Empty -> this
        is Node -> when {
            item < value -> Node(left.remove(item), value, right)
            item > value -> Node(left, value, right.remove(item))
            else -> left + right
        }
    }

    fun <R> fold(identity: R, transform: (R) -> (T) -> R, merge: (R) -> (R) -> R): R = when (this) {
        Empty -> identity
        is Node -> transform(
            merge(
                left.fold(identity, transform, merge)
            )(
                right.fold(identity, transform, merge)
            )
        )(value)
    }

    fun <R> foldLeft(identity: R, transform: (R) -> (T) -> R, merge: (R) -> (R) -> R): R = when (this) {
        Empty -> identity
        is Node -> merge(
            left.foldLeft(identity, transform, merge)
        )(
            transform(right.foldLeft(identity, transform, merge))(value)
        )
    }

    fun <R> foldRight(identity: R, f: (T) -> (R) -> R, g: (R) -> (R) -> R): R = when (this) {
        Empty -> identity
        is Node -> g(
            f(value)(left.foldRight(identity, f, g))
        )(
            right.foldRight(identity, f, g)
        )
    }

    fun <R> foldInOrder(identity: R, transform: (R) -> (T) -> (R) -> R): R = when (this) {
        Empty -> identity
        is Node -> transform(left.foldInOrder(identity, transform))(value)(right.foldInOrder(identity, transform))
    }

    fun <R> foldPreOrder(identity: R, transform: (T) -> (R) -> (R) -> R): R = when (this) {
        Empty -> identity
        is Node -> transform(value)(left.foldPreOrder(identity, transform))(right.foldPreOrder(identity, transform))
    }

    fun <R> foldPostOrder(identity: R, transform: (R) -> (R) -> (T) -> R): R = when (this) {
        Empty -> identity
        is Node -> transform(left.foldPostOrder(identity, transform))(right.foldPostOrder(identity, transform))(value)
    }

    operator fun plus(other: Tree<@UnsafeVariance T>): Tree<T> = when (this) {
        Empty -> other
        is Node -> when (other) {
            Empty -> this
            is Node -> when {
                value < other.value -> Node(
                    left, value, right + Node(
                        Empty, other.value, other.right
                    )
                ) + other.left

                value > other.value -> Node(
                    left + Node(
                        other.left, other.value, Empty
                    ), value, right
                ) + other.right

                else -> Node(left + other.left, value, right + other.right)
            }
        }
    }

    operator fun plus(element: @UnsafeVariance T): Tree<T> = when (this) {
        Empty -> Node(Empty, element, Empty)

        is Node -> when {
            element < value -> Node(left + element, value, right)
            element > value -> Node(left, value, right + element)
            else -> Node(left, element, right)
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

        // todo have to implement 10.10
        operator fun <T : Comparable<T>> invoke(left: Tree<T>, value: T, right: Tree<T>): Tree<T> = invoke()
    }
}

fun <T : Comparable<T>> ImmutableList<T>.toTree(): Tree<T> =
    foldLeft(Tree()) { acc ->
        { item -> acc + item }
    }
