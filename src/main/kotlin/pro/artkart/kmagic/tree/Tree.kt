package pro.artkart.kmagic.tree

import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.list.ImmutableList
import kotlin.math.abs
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
        is Node ->
            merge(
                transform(right.fold(identity, transform, merge))(value)
            )(
                left.fold(identity, transform, merge)
            )
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

    fun toImmutableList(): ImmutableList<@UnsafeVariance T> = when (this) {
        Empty -> ImmutableList()
        is Node -> foldPreOrder(ImmutableList()) { value ->
            { left ->
                { right -> left.concat(right).cons(value) }
            }
        }
    }

    fun toPseudoGraphicString(): String = when (this) {
        Empty -> "Empty"
        is Node -> "$value\n" + childrenToPseudoGraphicString("")
    }

    private fun childrenToPseudoGraphicString(prefix: String): String = when (this) {
        Empty -> ""
        is Node -> listOf(left, right).filter { !it.isEmpty() }.let { children ->
            children.mapIndexed { index, child ->
                val isLast = index == children.lastIndex
                val connector = if (isLast) "└── " else "├── "
                val childPrefix = prefix + if (isLast) "    " else "│   "
                "$prefix$connector${(child as Node).value}\n" + child.childrenToPseudoGraphicString(childPrefix)
            }.joinToString("")
        }
    }

    fun <R : Comparable<R>> map(transform: (T) -> R): Tree<R> = when (this) {
        Empty -> Empty
        is Node -> foldInOrder(invoke()) { left ->
            { item ->
                { right ->
                    invoke(left, transform(item), right)
                }
            }
        }
    }

    fun rotateRight(): Tree<T> = when (this) {
        Empty -> Empty
        is Node -> when (left) {
            Empty -> this
            is Node -> Node(left.left, left.value, Node(left.right, value, right))
        }
    }

    fun rotateLeft(): Tree<T> = when (this) {
        Empty -> Empty
        is Node -> when (right) {
            Empty -> this
            is Node -> Node(Node(left, value, right.left), right.value, right.right)
        }
    }

    fun toListInOrderRight(): ImmutableList<@UnsafeVariance T> = unBalanceLeft(ImmutableList(), this)

    fun balance(): Tree<T> = balance(toListInOrderRight().toTree())

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

        fun <T : Comparable<T>> balance(tree: Tree<T>): Tree<T> = unfold(tree) {
            when (it) {
                Empty -> Resolution.Empty
                is Node -> when {
                    it.size % 2 == 0 && abs(it.left.size - it.right.size) <= 1
                            || it.size % 2 != 0 && it.left.size == it.right.size
                        -> Resolution.Empty

                    else -> when {
                        it.left.size > it.right.size -> Resolution(it.rotateRight())
                        it.left.size < it.right.size -> Resolution(it.rotateLeft())
                        else -> Resolution.Empty
                    }
                }
            }
        }

        private fun <T : Comparable<T>> balanceV2(tree: Tree<T>): Tree<T> = when (tree) {
            Empty -> tree
            is Node -> when {
                tree.size % 2 == 0 && abs(tree.left.size - tree.right.size) <= 1
                        || tree.size % 2 != 0 && tree.left.size == tree.right.size
                    -> Node(balanceV2(tree.left), tree.value, balanceV2(tree.right))

                else -> if (tree.left.size > tree.right.size)
                    balanceV2(tree.rotateRight())
                else
                    balanceV2(tree.rotateLeft())
            }
        }

        fun <T> unfold(seed: T, f: (T) -> Resolution<T>): T {
            tailrec fun unfold(current: Pair<Resolution<T>, Resolution<T>>): Pair<Resolution<T>, Resolution<T>> {
                val next = current.second.flatMap { f(it) }
                return when (next) {
                    is Resolution.Success -> unfold(Pair(current.second, next))
                    else -> current
                }
            }
            return Resolution(seed).let { unfold(Pair(it, it)).second.getOrElse(seed) }
        }

        operator fun <T : Comparable<T>> invoke(): Tree<T> = Empty

        operator fun <T : Comparable<T>> invoke(vararg items: T): Tree<T> =
            items.fold(invoke()) { acc, item ->
                acc + item
            }

        operator fun <T : Comparable<T>> invoke(list: ImmutableList<T>): Tree<T> =
            list.foldLeft(invoke()) { acc -> { item -> acc + item } }

        operator fun <T : Comparable<T>> invoke(left: Tree<T>, value: T, right: Tree<T>): Tree<T> = when {
            ordered(left, value, right) -> Node(left, value, right)
            ordered(right, value, left) -> Node(right, value, left)
            else -> Tree(value) + left + right
        }

        private fun <T : Comparable<T>> ordered(left: Tree<T>, value: T, right: Tree<T>) =
            left.max().flatMap { lMax ->
                right.min().map { rMin ->
                    lMax < value && rMin > value
                }
            }.getOrElse { left.isEmpty() && right.isEmpty() }
                    || left.max().mapEmpty().flatMap {
                right.min().map { it > value }
            }.getOrElse(false)
                    || right.min().mapEmpty().flatMap {
                left.max().map { it < value }
            }.getOrElse(false)

        private tailrec fun <T : Comparable<T>> unBalanceLeft(acc: ImmutableList<T>, tree: Tree<T>): ImmutableList<T> =
            when (tree) {
                Empty -> acc
                is Node -> when (tree.right) {
                    Empty -> unBalanceLeft(acc.cons(tree.value), tree.left)
                    is Node -> unBalanceLeft(acc, tree.rotateLeft())
                }
            }

        private tailrec fun <T : Comparable<T>> unBalanceRight(acc: ImmutableList<T>, tree: Tree<T>): ImmutableList<T> =
            when (tree) {
                Empty -> acc
                is Node -> when (tree.left) {
                    Empty -> unBalanceRight(acc.cons(tree.value), tree.right)
                    is Node -> unBalanceRight(acc, tree.rotateRight())
                }
            }

        private fun log2nlz(n: Int): Int = when (n) {
            0 -> 0
            else -> 31 - Integer.numberOfLeadingZeros(n)
        }
    }
}

fun <T : Comparable<T>> ImmutableList<T>.toTree(): Tree<T> =
    foldLeft(Tree()) { acc ->
        { item -> acc + item }
    }

fun Tree<Int>.maxSum(): Int = when (this) {
    Tree.Empty -> 0
    is Tree.Node -> value + max(left.maxSum(), right.maxSum())
}

fun Tree<Int>.maxPathSum(): Int {
    var sum = 0
    fun Tree<Int>.maxPath(): Int = when (this) {
        Tree.Empty -> 0
        is Tree.Node -> {
            val lSum = max(left.maxPath(), 0)
            val rSum = max(right.maxPath(), 0)
            sum = max(sum, lSum + rSum + value)
            max(lSum, rSum) + value
        }
    }
    maxPath()
    return sum
}

fun main() {

    println(
        Tree(1, 2, 3).toPseudoGraphicString()
    )
}