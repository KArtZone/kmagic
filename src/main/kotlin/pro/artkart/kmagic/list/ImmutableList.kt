package pro.artkart.kmagic.list

import java.math.BigDecimal

sealed class ImmutableList<T> {

    internal object Nil : ImmutableList<Nothing>() {

        override fun isEmpty(): Boolean = true

        override fun size(): Int = 0

        override fun toString(): String = "[Nil]"
    }

    internal class Cons<T>(
        internal val head: T,
        internal val tail: ImmutableList<T>
    ) : ImmutableList<T>() {

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "[${toString("", this)}Nil]"

        override fun size(): Int = this.foldLeft(0) { acc -> { acc + 1 } }

        private tailrec fun toString(acc: String, list: ImmutableList<T>): String =
            when (list) {
                Nil -> acc
                is Cons -> toString("$acc${list.head}, ", list.tail)
            }
    }

    abstract fun isEmpty(): Boolean

    abstract fun size(): Int

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

        @Suppress("UNCHECKED_CAST")
        operator fun <T> invoke(vararg items: T): ImmutableList<T> =
            items.foldRight(Nil as ImmutableList<T>) { item, acc -> Cons(item, acc) }
    }
}

fun ImmutableList<Int>.sum(): Int {
    tailrec fun sum(acc: Int, list: ImmutableList<Int>): Int = when (list) {
        ImmutableList.Nil -> acc
        is ImmutableList.Cons -> sum(acc + list.head, list.tail)
    }
    return sum(0, this)
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
