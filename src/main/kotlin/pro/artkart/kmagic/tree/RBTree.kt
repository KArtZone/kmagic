package pro.artkart.kmagic.tree

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

    internal abstract class Empty<out T : Comparable<@UnsafeVariance T>> : RBTree<T>() {
        override val size: Int = 0
        override val height: Int = -1
        override val color: Color = Color.B
        override val isNodeR: Boolean = false
        override val isNodeB: Boolean = false
        override val left: RBTree<Nothing> by lazy { throw IllegalStateException("left called on Empty") }
        override val right: RBTree<Nothing> by lazy { throw IllegalStateException("right called on Empty") }
        override val value: Nothing by lazy { throw IllegalStateException("value called on Empty") }
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
        override val isNodeR: Boolean = color == Color.R
        override val isNodeB: Boolean = color == Color.B
        override fun toString(): String = "(Node $color $left $value $right)"
    }

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
