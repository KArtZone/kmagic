package pro.artkart.kmagic.tree

import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.list.ImmutableList
import pro.artkart.kmagic.optional.Option


sealed class Heap<out T : Comparable<@UnsafeVariance T>> {
    abstract val left: Resolution<Heap<T>>
    abstract val head: Resolution<T>
    abstract val right: Resolution<Heap<T>>
    abstract val rank: Int
    abstract val size: Int
    abstract val isEmpty: Boolean
    abstract fun tail(): Resolution<Heap<T>>
    abstract fun getV2(index: Int): Resolution<T>
    abstract fun get(index: Int): Resolution<T>
    abstract fun pop(): Option<Pair<T, Heap<T>>>
    abstract fun popV2(): Option<Pair<T, Heap<T>>>
    abstract fun toList(): ImmutableList<@UnsafeVariance T>

    abstract class Empty<out T : Comparable<@UnsafeVariance T>> : Heap<T>() {
        override val left: Resolution<Heap<T>> = Resolution(E)
        override val head: Resolution<T> = Resolution.failure("Empty heap doesn't have head")
        override val right: Resolution<Heap<T>> = Resolution(E)
        override val rank: Int = 0
        override val size: Int = 0
        override val isEmpty: Boolean = true
        override fun tail(): Resolution<Heap<T>> = Resolution.failure("tail() called on empty Heap")
        override fun get(index: Int): Resolution<T> = Resolution.failure("Index out of bounds")
        override fun getV2(index: Int): Resolution<T> = Resolution.failure("Index out of bounds")
        override fun pop(): Option<Pair<T, Heap<T>>> = Option.None
        override fun popV2(): Option<Pair<T, Heap<T>>> = Option.None
        override fun toList(): ImmutableList<@UnsafeVariance T> = ImmutableList()
        override fun toString(): String = "E"
    }

    object E : Empty<Nothing>()

    class Node<out T : Comparable<@UnsafeVariance T>>(
        val internalLeft: Heap<T>,
        val value: T,
        val internalRight: Heap<T>,
        override val rank: Int
    ) : Heap<T>() {
        override val left: Resolution<Heap<T>> = Resolution(internalLeft)
        override val head: Resolution<T> = Resolution(value)
        override val right: Resolution<Heap<T>> = Resolution(internalRight)
        override val size: Int = internalLeft.size + 1 + internalRight.size
        override val isEmpty: Boolean = false
        override fun tail(): Resolution<Heap<T>> = Resolution(merge(internalLeft, internalRight))
        override fun get(index: Int): Resolution<T> = when (index) {
            0 -> head
            else -> tail().flatMap { it.get(index - 1) }
        }

        override fun getV2(index: Int): Resolution<T> {
            tailrec fun getV2(heap: Heap<T>, remain: Int): Resolution<T> = when (heap) {
                is Empty -> Resolution.failure("get($index) Not found")
                else -> when {
                    remain > 0 -> getV2(heap.tail().getOrElse { E }, remain - 1)
                    else -> head
                }
            }

            fun get(maybeHeap: Resolution<Heap<T>>, remain: Int): Resolution<T> =
                maybeHeap.flatMap { heap ->
                    when {
                        remain > 0 -> get(heap.tail(), remain - 1)
                        else -> heap.head
                    }
                }

            return get(Resolution(this), index)
                .orElse { Resolution.failure("get($index) Not found") }
        }

        override fun pop(): Option<Pair<T, Heap<T>>> = Option(Pair(value, merge(internalLeft, internalRight)))

        override fun popV2(): Option<Pair<T, Heap<T>>> = head.flatMap { hd ->
            tail().map { tl ->
                Pair(hd, tl)
            }
        }.map { Option(it) }
            .getOrElse { Option.None }

        override fun toList(): ImmutableList<@UnsafeVariance T> = foldLeft<ImmutableList<T>>(ImmutableList()) { acc ->
            { item ->
                acc.cons(item)
            }
        }.reverseV2()

        override fun toString(): String = "(Node r$rank value=$value l=$left r=$right)"
    }

    fun <R> foldLeft(identity: R, f: (R) -> (T) -> R): R = unfold(this, Heap<T>::pop, identity, f)

    operator fun plus(item: @UnsafeVariance T): Heap<T> = mergeV2(this, invoke(item))

    companion object {

        fun <T : Comparable<T>> merge(head: T, first: Heap<T>, second: Heap<T>): Heap<T> = when {
            first.rank >= second.rank -> Node(first, head, second, second.rank + 1)
            else -> Node(second, head, first, first.rank + 1)
        }

        fun <T : Comparable<T>> merge(first: Heap<T>, second: Heap<T>): Heap<T> =
            first.head.flatMap { fh ->
                second.head.flatMap { sh ->
                    when {
                        fh <= sh -> first.left.flatMap { fl ->
                            first.right.map { fr ->
                                merge(fh, fl, merge(fr, second))
                            }
                        }

                        else -> second.left.flatMap { sl ->
                            second.right.map { sr ->
                                merge(sh, sl, merge(first, sr))
                            }
                        }
                    }
                }
            }.getOrElse(
                when (first) {
                    E -> second
                    else -> first
                }
            )

        fun <T : Comparable<T>> mergeV2(first: Heap<T>, second: Heap<T>): Heap<T> = when (first) {
            is Empty -> second
            is Node -> when (second) {
                is Empty -> first
                is Node -> when {
                    first.value > second.value -> mergeV2(second, first)
                    else -> when {
                        first.internalLeft.rank > first.internalRight.rank ->
                            mergeV2(first.internalRight, second).let { newRight ->
                                Node(first.internalLeft, first.value, newRight, newRight.rank + 1)
                            }

                        else -> Node(mergeV2(first.internalLeft, second), first.value, first.internalRight, first.rank)
                    }
                }
            }
        }

        operator fun <T : Comparable<T>> invoke(): Heap<T> = E

        operator fun <T : Comparable<T>> invoke(item: T): Heap<T> =
            Node(E, item, E, 1)
    }
}

fun <T, S, R> unfold(seed: S, getNext: (S) -> Option<Pair<T, S>>, identity: R, f: (R) -> (T) -> R): R {
    tailrec fun unfold(acc: R, current: S): R =
        when (val next = getNext(current)) {
            Option.None -> acc
            is Option.Some -> unfold(f(acc)(next.value.first), next.value.second)
        }
    return unfold(identity, seed)
}
