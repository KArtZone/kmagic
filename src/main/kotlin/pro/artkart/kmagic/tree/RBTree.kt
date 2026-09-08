package pro.artkart.kmagic.tree

import pro.artkart.kmagic.tree.Color.B
import pro.artkart.kmagic.tree.Color.R
import kotlin.math.max

sealed class RBTree<out T : Comparable<@UnsafeVariance T>> {
    abstract val size: Int
    abstract val height: Int
    abstract val color: Color
    abstract val isNodeR: Boolean
    abstract val isNodeB: Boolean
    abstract val left: RBTree<T>
    abstract val right: RBTree<T>
    abstract val value: T
    abstract fun add(newVal: @UnsafeVariance T): RBTree<T>
    abstract fun blacken(): RBTree<T>

    protected fun balance(
        color: Color,
        left: RBTree<@UnsafeVariance T>,
        value: @UnsafeVariance T,
        right: RBTree<@UnsafeVariance T>
    ): RBTree<T> = when {
        color == B && left.isNodeR && left.left.isNodeR ->
            Node(R, left.left.blacken(), left.value, Node(B, left.right, value, right))

        color == B && left.isNodeR && left.right.isNodeR ->
            Node(
                R, Node(B, left.left, left.value, left.right.left), left.right.value,
                Node(B, left.right.right, value, right)
            )

        color == B && right.isNodeR && right.left.isNodeR ->
            Node(
                R, Node(B, left, value, right.left.left), right.left.value,
                Node(B, right.left.right, right.value, right.right)
            )

        color == B && right.isNodeR && right.right.isNodeR ->
            Node(R, Node(B, left, value, right.left), right.value, right.right.blacken())

        else -> Node(color, left, value, right)
    }

    internal abstract class Empty<out T : Comparable<@UnsafeVariance T>> : RBTree<T>() {
        override val size: Int = 0
        override val height: Int = -1
        override val color: Color = B
        override val isNodeR: Boolean = false
        override val isNodeB: Boolean = false
        override val left: RBTree<Nothing> by lazy { throw IllegalStateException("left called on Empty") }
        override val right: RBTree<Nothing> by lazy { throw IllegalStateException("right called on Empty") }
        override val value: Nothing by lazy { throw IllegalStateException("value called on Empty") }
        override fun add(newVal: @UnsafeVariance T): RBTree<T> = Node(R, E, newVal, E)
        override fun blacken(): RBTree<T> = E
        override fun toString(): String = "E"
    }

    internal object E : Empty<Nothing>()

    internal class Node<out T : Comparable<@UnsafeVariance T>>(
        override val color: Color,
        override val left: RBTree<T>,
        override val value: T,
        override val right: RBTree<T>
    ) : RBTree<T>() {
        override val size: Int = left.size + 1 + right.size
        override val height: Int = 1 + max(left.height, right.height)
        override val isNodeR: Boolean = color == R
        override val isNodeB: Boolean = color == B
        override fun add(newVal: @UnsafeVariance T): RBTree<T> = when {
            newVal < value -> balance(color, left.add(newVal), value, right)
            newVal > value -> balance(color, left, value, right.add(newVal))
            else -> Node(color, left, value, right)
        }

        override fun blacken(): RBTree<T> = Node(B, left, value, right)
        override fun toString(): String = "(Node $color $left $value $right)"
    }

    operator fun plus(newVal: @UnsafeVariance T): RBTree<T> = add(newVal).blacken()

    companion object {

        operator fun <T : Comparable<T>> invoke(): RBTree<T> = E
    }
}

sealed class Color {
    internal object R : Color() {
        override fun toString(): String = "R"
    }

    internal object B : Color() {
        override fun toString(): String = "B"
    }
}
