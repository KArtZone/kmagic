package pro.artkart.kmagic.list

import pro.artkart.kmagic.exception.Either
import pro.artkart.kmagic.exception.Resolution
import java.math.BigDecimal
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService

sealed class ImmutableList<T> {

    internal object Nil : ImmutableList<Nothing>() {

        override val size: Int = 0

        override fun isEmpty(): Boolean = true

        override fun headSafe(): Resolution<Nothing> = Resolution.Empty

        override fun <R> foldToPair(
            identity: R,
            zero: R,
            f: (R) -> (Nothing) -> R
        ): Pair<R, ImmutableList<Nothing>> = Pair(identity, Nil)

        override fun toString(): String = "[Nil]"

        override fun equals(other: Any?): Boolean = other === Nil

        override fun hashCode(): Int = 0
    }

    internal data class Cons<T>(
        val head: T,
        val tail: ImmutableList<T>
    ) : ImmutableList<T>() {

        override val size: Int = 1 + tail.size

        override fun isEmpty(): Boolean = false

        override fun headSafe(): Resolution<T> = Resolution(head)

        override fun <R> foldToPair(
            identity: R,
            zero: R,
            f: (R) -> (T) -> R
        ): Pair<R, ImmutableList<T>> {
            tailrec fun foldToPair(acc: R, list: ImmutableList<T>): Pair<R, ImmutableList<T>> = when (list) {
                Nil -> Pair(acc, list)
                is Cons -> if (acc == zero)
                    Pair(acc, list)
                else
                    foldToPair(f(acc)(list.head), list.tail)
            }
            return foldToPair(identity, this)
        }

        override fun toString(): String = "[${toString("", this)}Nil]"

        private tailrec fun toString(acc: String, list: ImmutableList<T>): String =
            when (list) {
                Nil -> acc
                is Cons -> toString("$acc${list.head}, ", list.tail)
            }
    }

    abstract val size: Int

    abstract fun isEmpty(): Boolean

    abstract fun headSafe(): Resolution<T>

    abstract fun <R> foldToPair(identity: R, zero: R, f: (R) -> (T) -> R): Pair<R, ImmutableList<T>>

    fun lastSafe(): Resolution<T> = foldLeft(Resolution()) { { Resolution(it) } }

    fun headSafeV2(): Resolution<T> = when (this) {
        Nil -> Resolution.Empty
        is Cons -> Resolution(head)
    }

    fun headSafeV3(): Resolution<T> =
        coFoldRight(Resolution()) { { _ -> Resolution(it) } }

    fun lastSafeV2(): Resolution<T> = when (this) {
        Nil -> Resolution.Empty
        else -> drop(size - 1).headSafe()
    }

    fun lastSafeV3(): Resolution<T> = when (this) {
        Nil -> Resolution.Empty
        is Cons -> when (tail) {
            Nil -> Resolution(head)
            is Cons -> tail.lastSafeV3()
        }
    }

    fun cons(item: T): ImmutableList<T> = Cons(item, this)

    fun setHead(item: T): ImmutableList<T> = when (this) {
        Nil -> throw UnsupportedOperationException("Operation not allowed")
        is Cons -> tail.cons(item)
    }

    fun drop(n: Int): ImmutableList<T> {
        tailrec fun drop(list: ImmutableList<T>, remain: Int): ImmutableList<T> = when {
            remain > 0 && list is Cons -> drop(list.tail, remain - 1)
            else -> list
        }
        return drop(this, n)
    }

    fun dropWhile(p: (T) -> Boolean): ImmutableList<T> {
        fun dropWhile(list: ImmutableList<T>): ImmutableList<T> = when {
            list is Cons && p(list.head) -> dropWhile(list.tail)
            else -> list
        }
        return dropWhile(this)
    }

    fun concat(list: ImmutableList<T>): ImmutableList<T> {
        return concatRight(this, list)
    }

    fun init(): ImmutableList<T> = init(this)

    fun reverse(): ImmutableList<T> = reverse(Nil as ImmutableList<T>, this)

    fun reverseV2(): ImmutableList<T> = foldLeft(invoke()) { acc ->
        { item -> acc.cons(item) }
    }

    fun <R> foldRight(identity: R, transform: (T) -> (R) -> R): R = foldRight(this, identity, transform)

    fun <R> foldRightV2(identity: R, transform: (T) -> (R) -> R): R = this.reverseV2()
        .foldLeft(identity) { acc ->
            { item -> transform(item)(acc) }
        }

    fun <R> coFoldRight(identity: R, transform: (T) -> (R) -> R): R =
        coFoldRight(this.reverseV2(), identity, transform)

    fun <R> foldLeft(identity: R, transform: (R) -> (T) -> R): R = foldLeft(this, identity, transform)

    fun <R> foldLeft(identity: R, zero: R, transform: (R) -> (T) -> R): R {
        tailrec fun foldLeft(acc: R, list: ImmutableList<T>): R = if (acc == zero)
            acc
        else
            when (list) {
                Nil -> identity
                is Cons -> foldLeft(transform(acc)(list.head), list.tail)
            }

        return foldLeft(identity, this)
    }

    fun <R> foldLeft(identity: R, p: (R) -> Boolean, transform: (R) -> (T) -> R): R {
        tailrec fun foldLeft(acc: R, list: ImmutableList<T>): R = if (p(acc))
            acc
        else
            when (list) {
                Nil -> identity
                is Cons -> foldLeft(transform(acc)(list.head), list.tail)
            }
        return foldLeft(identity, this)
    }

    fun <R> map(transform: (T) -> R): ImmutableList<R> = this.coFoldRight(invoke()) { item ->
        { acc -> acc.cons(transform(item)) }
    }

    fun filter(p: (T) -> Boolean): ImmutableList<T> = this.coFoldRight(invoke()) { item ->
        { acc ->
            if (p(item))
                acc.cons(item)
            else
                acc
        }
    }

    fun filterV2(p: (T) -> Boolean): ImmutableList<T> = this.flatMap { if (p(it)) invoke(it) else invoke() }

    fun <R> flatMap(transform: (T) -> ImmutableList<R>): ImmutableList<R> = flatten(this.map(transform))

    fun <U, V> unzip(f: (T) -> Pair<U, V>): Pair<ImmutableList<U>, ImmutableList<V>> =
        coFoldRight(Pair(invoke(), invoke())) { item ->
            { acc ->
                f(item).let { pair ->
                    Pair(acc.first.cons(pair.first), acc.second.cons(pair.second))
                }
            }
        }

    fun getAt(index: Int): Resolution<T> {
        tailrec fun getAt(list: ImmutableList<T>, idx: Int): Resolution<T> =
            when (list) {
                Nil -> Resolution.Empty
                is Cons ->
                    if (idx == index)
                        Resolution(list.head)
                    else
                        getAt(list.tail, idx + 1)
            }
        return getAt(this, 0)
    }

    fun getAtV2(index: Int): Resolution<T> = foldLeft(Pair<Resolution<T>, Int>(Resolution.Empty, 0)) { acc ->
        { item ->
            if (acc.second == index)
                Pair(Resolution(item), acc.second + 1)
            else
                Pair(acc.first, acc.second + 1)
        }
    }.first

    fun getAtV3(index: Int): Resolution<T> = foldLeft(
        PairAcc(Resolution.Empty), PairAcc(Resolution<T>(), index)
    ) { acc ->
        { item ->
            PairAcc(Resolution(item), acc.index + 1)
        }
    }.acc

    fun getAtV4(index: Int): Resolution<T> = foldLeft<PairAcc<T>>(
        PairAcc(Resolution.Empty),
        { it == PairAcc(Resolution.Empty, index) })
    { acc ->
        { item ->
            PairAcc(Resolution(item), acc.index + 1)
        }
    }.acc

    fun split(index: Int): ImmutableList<ImmutableList<T>> {
        tailrec fun split(idx: Int, left: ImmutableList<T>, right: ImmutableList<T>): ImmutableList<ImmutableList<T>> =
            when (right) {
                Nil -> invoke(left.reverseV2(), right)
                is Cons -> if (idx == index)
                    invoke(left.reverseV2(), right)
                else
                    split(idx + 1, left.cons(right.head), right.tail)
            }
        return split(0, invoke(), this)
    }

    fun splitAt(index: Int): Pair<ImmutableList<T>, ImmutableList<T>> {
        tailrec fun splitAt(
            idx: Int,
            acc: ImmutableList<T>,
            list: ImmutableList<T>
        ): Pair<ImmutableList<T>, ImmutableList<T>> = when (list) {
            Nil -> Pair(acc.reverseV2(), list)
            is Cons -> if (idx == index)
                Pair(acc.reverseV2(), list)
            else
                splitAt(idx + 1, acc.cons(list.head), list.tail)
        }
        return when {
            index < 0 -> splitAt(0)
            index > size -> splitAt(size)
            else -> splitAt(0, invoke(), this)
        }
    }

    fun splitAtV2(index: Int): Pair<ImmutableList<T>, ImmutableList<T>> = this.foldLeft(
        TripleAcc(invoke<T>(), invoke(), 0)
    ) { acc ->
        { item ->
            if (acc.index < index)
                TripleAcc(acc.left.cons(item), acc.right, acc.index + 1)
            else
                TripleAcc(acc.left, acc.right.cons(item), acc.index + 1)
        }
    }.let { Pair(it.left.reverseV2(), it.right.reverseV2()) }

    fun splitAtV3(index: Int): Pair<ImmutableList<T>, ImmutableList<T>> = this.foldLeft(
        TripleAcc(invoke(), this),
        TripleAcc(invoke(), invoke(), index - 1)
    ) { acc ->
        { item ->
            TripleAcc(
                acc.left.cons(item),
                (acc.right as Cons).tail,
                acc.index + 1
            )
        }
    }.let { Pair(it.left.reverseV2(), it.right) }

    fun splitAtV4(index: Int): Pair<ImmutableList<T>, ImmutableList<T>> = foldToPair(
        PairWithList(invoke()),
        PairWithList(this, index - 1)
    ) { acc ->
        { item ->
            PairWithList(acc.list.cons(item), acc.index + 1)
        }
    }.let { (pair, list) -> Pair(pair.list.reverseV2(), list) }

    fun startsWith(sub: ImmutableList<T>): Boolean {
        tailrec fun statsWith(list: ImmutableList<T>, subList: ImmutableList<T>): Boolean =
            when (subList) {
                Nil -> true
                is Cons -> when (list) {
                    Nil -> false
                    is Cons -> list.head == subList.head && statsWith(list.tail, subList.tail)
                }
            }
        return statsWith(this, sub)
    }

    fun hasSublist(sub: ImmutableList<T>): Boolean {
        tailrec fun hasSublist(list: ImmutableList<T>): Boolean = when (list) {
            Nil -> sub.isEmpty()
            is Cons -> list.startsWith(sub) || hasSublist(list.tail)
        }
        return hasSublist(this)
    }

    fun <R> groupBy(f: (T) -> R): Map<R, ImmutableList<T>> {
        tailrec fun groupBy(acc: Map<R, ImmutableList<T>>, list: ImmutableList<T>): Map<R, ImmutableList<T>> =
            when (list) {
                Nil -> acc
                is Cons -> {
                    val key = f(list.head)
                    groupBy(acc + (key to acc.getOrDefault(key, invoke()).cons(list.head)), list.tail)
                }
            }
        return groupBy(mutableMapOf(), this.reverseV2())
    }

    fun <R> groupByV2(f: (T) -> R): Map<R, ImmutableList<T>> {
        fun groupByV2(list: ImmutableList<T>): Map<R, ImmutableList<T>> =
            when (list) {
                Nil -> mapOf()
                is Cons -> {
                    val key = f(list.head)
                    val acc = groupByV2(list.tail)
                    acc + (key to acc.getOrDefault(key, invoke()).cons(list.head))
                }
            }
        return groupByV2(this)
    }

    fun <R> groupByV3(f: (T) -> R): Map<R, ImmutableList<T>> = reverseV2().foldLeft(mapOf()) { acc ->
        { item ->
            f(item).let { key ->
                acc + (key to (acc[key] ?: invoke()).cons(item))
            }
        }
    }

    fun <R> groupByV4(f: (T) -> R): Map<R, ImmutableList<T>> = coFoldRight(mapOf()) { item ->
        { acc ->
            f(item).let { key ->
                acc + (key to (acc[key] ?: invoke()).cons(item))
            }
        }
    }

    fun exists(p: (T) -> Boolean): Boolean {
        tailrec fun exists(list: ImmutableList<T>): Boolean = when (list) {
            Nil -> false
            is Cons -> p(list.head) || exists(list.tail)
        }
        return exists(this)
    }

    fun existsV2(p: (T) -> Boolean): Boolean = foldLeft(
        identity = false,
        zero = true
    ) { { p(it) } }

    fun forAll(p: (T) -> Boolean): Boolean = foldLeft(
        identity = true,
        zero = false
    ) { { p(it) } }

    fun divide(depth: Int): ImmutableList<ImmutableList<T>> {
        tailrec fun divide(
            currentDepth: Int,
            list: ImmutableList<ImmutableList<T>>
        ): ImmutableList<ImmutableList<T>> = when (list) {
            Nil -> list
            is Cons -> if (list.head.size < 2 || currentDepth == depth)
                list
            else
                divide(
                    currentDepth + 1,
                    list.flatMap {
                        it.split(it.size / 2)
                    })
        }

        return if (this.isEmpty())
            ImmutableList(this)
        else
            divide(0, ImmutableList(this))
    }

    fun <R> parFoldLeft(
        es: ExecutorService,
        identity: R,
        f: (R) -> (T) -> R,
        m: (R) -> (R) -> R
    ): Resolution<R> = try {
        divide(1024)
            .map { list -> es.submit<R> { list.foldLeft(identity, f) } }
            .map { future ->
                try {
                    future.get()
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                } catch (e: ExecutionException) {
                    throw RuntimeException(e)
                }
            }
            .foldLeft(identity, m)
            .let { Resolution(it) }
    } catch (e: Exception) {
        Resolution.failure(RuntimeException(e))
    }

    fun <R> parMap(es: ExecutorService, f: (T) -> R): Resolution<ImmutableList<R>> =
        try {
            this.map { es.submit<R> { f(it) } }
                .map { future ->
                    try {
                        future.get()
                    } catch (e: InterruptedException) {
                        throw RuntimeException(e)
                    } catch (e: ExecutionException) {
                        throw RuntimeException(e)
                    }
                }
                .let { Resolution(it) }
        } catch (e: Exception) {
            Resolution.failure(RuntimeException(e))
        }

    companion object {

        tailrec fun <T> concatLeft(left: ImmutableList<T>, right: ImmutableList<T>): ImmutableList<T> =
            when (left) {
                Nil -> right
                is Cons -> concatLeft(left.tail, right.cons(left.head))
            }

        fun <T> concatRight(left: ImmutableList<T>, right: ImmutableList<T>): ImmutableList<T> = when (left) {
            Nil -> right
            is Cons -> concatRight(left.tail, right).cons(left.head)
        }

        tailrec fun <T> reverse(acc: ImmutableList<T>, list: ImmutableList<T>): ImmutableList<T> = when (list) {
            Nil -> acc
            is Cons -> reverse(acc.cons(list.head), list.tail)
        }

        fun <T> init(list: ImmutableList<T>): ImmutableList<T> = list.reverse().drop(1).reverse()

        fun <T> initRight(list: ImmutableList<T>): ImmutableList<T> = when (list) {
            Nil -> list
            is Cons -> if (list.tail.isEmpty())
                invoke()
            else
                initRight(list.tail).cons(list.head)
        }

        fun <T, R> foldRight(list: ImmutableList<T>, identity: R, transform: (T) -> (R) -> R): R = when (list) {
            Nil -> identity
            is Cons -> transform(list.head)(foldRight(list.tail, identity, transform))
        }

        tailrec fun <T, R> foldLeft(list: ImmutableList<T>, acc: R, transform: (R) -> (T) -> R): R = when (list) {
            Nil -> acc
            is Cons -> foldLeft(list.tail, transform(acc)(list.head), transform)
        }

        private tailrec fun <T, R> coFoldRight(list: ImmutableList<T>, acc: R, transform: (T) -> (R) -> R): R =
            when (list) {
                Nil -> acc
                is Cons -> coFoldRight(list.tail, transform(list.head)(acc), transform)
            }

        fun <T : Comparable<T>> max(list: ImmutableList<T>): Either<String, T> = when (list) {
            Nil -> Either.left("Max called in an empty list")
            is Cons -> Either.right(list.foldLeft(list.head) { acc ->
                { item -> if (acc >= item) acc else item }
            })
        }

        @Suppress("UNCHECKED_CAST")
        operator fun <T> invoke(vararg items: T): ImmutableList<T> =
            items.foldRight(Nil as ImmutableList<T>) { item, acc -> Cons(item, acc) }
    }
}

class PairAcc<out T>(
    val acc: Resolution<T>,
    val index: Int = -1
) {

    override fun equals(other: Any?): Boolean =
        other === this || (other is PairAcc<T> && other.index == index)

    override fun hashCode(): Int = (index + 42) * 71 + 67
}

class TripleAcc<T>(
    val left: ImmutableList<T>,
    val right: ImmutableList<T>,
    val index: Int = -1
) {

    override fun equals(other: Any?): Boolean =
        other === this || (other is TripleAcc<T> && other.index == index)

    override fun hashCode(): Int = (index + 42) * 71 + 67
}

class PairWithList<T>(
    val list: ImmutableList<T>,
    val index: Int = -1
) {

    override fun equals(other: Any?): Boolean =
        other === this || (other is PairWithList<T> && other.index == index)

    override fun hashCode(): Int = (index + 42) * 71 + 67
}

fun ImmutableList<Int>.sum(): Int {
    tailrec fun sum(acc: Int, list: ImmutableList<Int>): Int = when (list) {
        ImmutableList.Nil -> acc
        is ImmutableList.Cons -> sum(acc + list.head, list.tail)
    }
    return sum(0, this)
}

fun ImmutableList<Double>.sum(): Double = foldLeft(0.0) { acc ->
    { item -> acc + item }
}

fun <T> concatFoldRight(left: ImmutableList<T>, right: ImmutableList<T>): ImmutableList<T> =
    left.foldRight(right) { item ->
        { acc ->
            acc.cons(item)
        }
    }

fun <T> concatFoldLeft(left: ImmutableList<T>, right: ImmutableList<T>): ImmutableList<T> =
    left.reverseV2().foldLeft(right) { acc -> acc::cons }

fun ImmutableList<Int>.factorial(): BigDecimal = foldRightV2(BigDecimal.ONE) { item ->
    { acc ->
        acc * item.toBigDecimal()
    }
}

fun ImmutableList<Int>.factorialV2(): BigDecimal = this.coFoldRight(BigDecimal.ONE) { item ->
    { acc ->
        acc * item.toBigDecimal()
    }
}

fun <T> flatten(lists: ImmutableList<ImmutableList<T>>): ImmutableList<T> =
    lists.coFoldRight(ImmutableList()) { list -> list::concat }

fun triple(list: ImmutableList<Int>): ImmutableList<Int> =
    list.coFoldRight(ImmutableList()) { item -> { acc -> acc.cons(item * 3) } }

fun doubleToString(list: ImmutableList<Double>): ImmutableList<String> =
    list.coFoldRight(ImmutableList()) { item ->
        { acc -> acc.cons(item.toString()) }
    }
