package pro.artkart.kmagic.tree

import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.list.ImmutableList
import kotlin.math.abs
import kotlin.math.max

sealed class BST<out T : Comparable<@UnsafeVariance T>> {

    abstract val size: Int

    abstract val height: Int

    abstract fun isEmpty(): Boolean

    var autoBalanced: Boolean = false

    internal object Empty : BST<Nothing>() {
        override val size: Int = 0
        override val height: Int = -1
        override fun isEmpty(): Boolean = true
        override fun toString(): String = "E"
    }

    internal data class Node<out T : Comparable<@UnsafeVariance T>>(
        val left: BST<T>,
        val value: T,
        val right: BST<T>
    ) : BST<T>() {
        override val size: Int = 1 + left.size + right.size
        override val height: Int = 1 + max(left.height, right.height)
        override fun isEmpty(): Boolean = false

        //        override fun toString(): String = "(Node $left $value $right)"
        override fun toString(): String = "\n${toPseudoGraphicString()}"
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

    fun remove(item: @UnsafeVariance T): BST<T> = when (this) {
        Empty -> this
        is Node -> when {
            item < value -> {
                val node = Node(left.remove(item), value, right)
                if (autoBalanced) balance(node) else node
            }

            item > value -> {
                val node = Node(left, value, right.remove(item))
                if (autoBalanced) balance(node) else node
            }

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

    fun <R : Comparable<R>> map(transform: (T) -> R): BST<R> = when (this) {
        Empty -> Empty
        is Node -> foldInOrder(invoke()) { left ->
            { item ->
                { right ->
                    invoke(left, transform(item), right)
                }
            }
        }
    }

    fun rotateRight(): BST<T> = when (this) {
        Empty -> Empty
        is Node -> when (left) {
            Empty -> this
            is Node -> Node(left.left, left.value, Node(left.right, value, right))
        }
    }

    fun rotateLeft(): BST<T> = when (this) {
        Empty -> Empty
        is Node -> when (right) {
            Empty -> this
            is Node -> Node(Node(left, value, right.left), right.value, right.right)
        }
    }

    fun toListInOrderRight(): ImmutableList<@UnsafeVariance T> = unBalanceLeft(ImmutableList(), this)

    fun balance(): BST<T> = balance(toListInOrderRight().toBST())

    operator fun plus(other: BST<@UnsafeVariance T>): BST<T> = when (this) {
        Empty -> other
        is Node -> when (other) {
            Empty -> this
            is Node -> when {
                value < other.value -> {
                    val node = Node(
                        left, value, right + Node(
                            Empty, other.value, other.right
                        )
                    ) + other.left
                    when (node) {
                        is Node if (autoBalanced) -> balance(node)

                        else -> node
                    }
                }

                value > other.value -> {
                    val node = Node(
                        left + Node(
                            other.left, other.value, Empty
                        ), value, right
                    ) + other.right
                    when (node) {
                        is Node if (autoBalanced) -> balance(node)
                        else -> node
                    }
                }

                else -> Node(left + other.left, value, right + other.right)
            }
        }
    }

    operator fun plus(element: @UnsafeVariance T): BST<T> = when (this) {
        Empty -> Node(Empty, element, Empty)

        is Node -> when {
            element < value -> {
                val node = Node(left + element, value, right)
                if (autoBalanced) balance(node) else node
            }

            element > value -> {
                val node = Node(left, value, right + element)
                if (autoBalanced) balance(node) else node
            }

            else -> Node(left, element, right)
        }
    }

    private fun balance(node: Node<T>): BST<T> {
        val diff = node.left.height - node.right.height
        return when {
            abs(diff) > 1 -> when {
                diff < 0 -> when (node.right) {
                    is Node if (node.right.left.height > node.right.right.height) ->
                        Node(node.left, node.value, node.right.rotateRight())

                    else -> node
                }.rotateLeft()

                diff > 0 -> when (node.left) {
                    is Node if (node.left.left.height < node.left.right.height) ->
                        Node(node.left.rotateLeft(), node.value, node.right)

                    else -> node
                }.rotateRight()

                else -> node
            }

            else -> node
        }
    }

    companion object {

        private fun <T : Comparable<T>> balance(bst: BST<T>): BST<T> = when (bst) {
            Empty -> bst
            is Node -> when {
                isBalanced(bst) -> Node(balance(bst.left), bst.value, balance(bst.right))

                else -> if (bst.left.size > bst.right.size)
                    balance(bst.rotateRight())
                else
                    balance(bst.rotateLeft())
            }
        }

        @Suppress("unused")
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

        operator fun <T : Comparable<T>> invoke(): BST<T> = Empty

        operator fun <T : Comparable<T>> invoke(vararg items: T): BST<T> =
            items.fold(invoke()) { acc, item ->
                acc + item
            }

        operator fun <T : Comparable<T>> invoke(list: ImmutableList<T>): BST<T> =
            list.foldLeft(invoke()) { acc -> { item -> acc + item } }

        operator fun <T : Comparable<T>> invoke(left: BST<T>, value: T, right: BST<T>): BST<T> = when {
            ordered(left, value, right) -> Node(left, value, right)
            ordered(right, value, left) -> Node(right, value, left)
            else -> BST(value) + left + right
        }

        private fun <T : Comparable<T>> ordered(left: BST<T>, value: T, right: BST<T>) =
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

        private tailrec fun <T : Comparable<T>> unBalanceLeft(acc: ImmutableList<T>, bst: BST<T>): ImmutableList<T> =
            when (bst) {
                Empty -> acc
                is Node -> when (bst.right) {
                    Empty -> unBalanceLeft(acc.cons(bst.value), bst.left)
                    is Node -> unBalanceLeft(acc, bst.rotateLeft())
                }
            }

        private tailrec fun <T : Comparable<T>> unBalanceRight(acc: ImmutableList<T>, bst: BST<T>): ImmutableList<T> =
            when (bst) {
                Empty -> acc
                is Node -> when (bst.left) {
                    Empty -> unBalanceRight(acc.cons(bst.value), bst.right)
                    is Node -> unBalanceRight(acc, bst.rotateRight())
                }
            }

        @Suppress("unused")
        private fun log2nlz(n: Int): Int = when (n) {
            0 -> 0
            else -> 31 - Integer.numberOfLeadingZeros(n)
        }

        private fun <T : Comparable<T>> isBalanced(bst: BST<T>): Boolean = when (bst) {
            Empty -> true
            is Node -> abs(bst.left.height - bst.right.height).let { diff ->
                when {
                    bst.size % 2 == 0 -> diff == 1
                    bst.size % 2 == 1 -> diff == 0
                    else -> false
                }
            }
        }
    }
}

fun <T : Comparable<T>> ImmutableList<T>.toBST(): BST<T> =
    foldLeft(BST()) { acc ->
        { item -> acc + item }
    }

fun BST<Int>.maxSum(): Int = when (this) {
    BST.Empty -> 0
    is BST.Node -> value + max(left.maxSum(), right.maxSum())
}

fun BST<Int>.maxPathSum(): Int {
    var sum = 0
    fun BST<Int>.maxPath(): Int = when (this) {
        BST.Empty -> 0
        is BST.Node -> {
            val lSum = max(left.maxPath(), 0)
            val rSum = max(right.maxPath(), 0)
            sum = max(sum, lSum + rSum + value)
            max(lSum, rSum) + value
        }
    }
    maxPath()
    return sum
}
