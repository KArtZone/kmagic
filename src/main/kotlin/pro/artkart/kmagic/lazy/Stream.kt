package pro.artkart.kmagic.lazy

import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.list.ImmutableList

sealed class Stream<out T> {

    abstract fun isEmpty(): Boolean

    abstract fun head(): Resolution<T>

    abstract fun tail(): Resolution<Stream<T>>

    abstract fun takeAtMost(n: Int): Stream<T>

    abstract fun takeWhile(p: (T) -> Boolean): Stream<T>

    private object Empty : Stream<Nothing>() {
        override fun isEmpty(): Boolean = true
        override fun head(): Resolution<Nothing> = Resolution()
        override fun tail(): Resolution<Stream<Nothing>> = Resolution()
        override fun takeAtMost(n: Int): Stream<Nothing> = Empty
        override fun takeWhile(p: (Nothing) -> Boolean): Stream<Nothing> = Empty
    }

    private class Cons<T>(
        internal val hd: Deferred<T>,
        internal val tl: Deferred<Stream<T>>
    ) : Stream<T>() {

        override fun isEmpty(): Boolean = false

        override fun head(): Resolution<T> = Resolution(hd())

        override fun tail(): Resolution<Stream<T>> = Resolution(tl())

        override fun takeAtMost(n: Int): Stream<T> = when {
            n > 0 -> cons(hd, Deferred { tl().takeAtMost(n - 1) })
            else -> Empty
        }

        override fun takeWhile(p: (T) -> Boolean): Stream<T> =
            if (p(hd()))
                cons(hd, Deferred { tl().takeWhile(p) })
            else
                Empty
    }

    fun repeat(f: () -> @UnsafeVariance T): Stream<T> =
        cons(Deferred { f() }, Deferred { repeat(f) })

    fun dropAtMost(n: Int): Stream<T> = dropAtMost(this, n)

    fun dropWhile(p: (T) -> Boolean): Stream<T> = dropWhile(this, p)

    fun exists(p: (T) -> Boolean): Boolean = exists(this, p)

    fun toList(): ImmutableList<@UnsafeVariance T> = toList(stream = this).reverseV2()

    companion object {

        tailrec fun <T> dropAtMost(stream: Stream<T>, index: Int): Stream<T> = when (stream) {
            Empty -> stream
            is Cons -> if (index > 0)
                dropAtMost(stream.tl(), index - 1)
            else
                stream
        }

        tailrec fun <T> dropWhile(stream: Stream<T>, p: (T) -> Boolean): Stream<T> = when (stream) {
            Empty -> Empty
            is Cons -> if (p(stream.hd()))
                dropWhile(stream.tl(), p)
            else
                stream
        }

        tailrec fun <T> exists(stream: Stream<T>, p: (T) -> Boolean): Boolean = when (stream) {
            Empty -> false
            is Cons -> p(stream.hd()) || exists(stream.tl(), p)
        }

        tailrec fun <T> toList(acc: ImmutableList<T> = ImmutableList(), stream: Stream<T>): ImmutableList<T> =
            when (stream) {
                Empty -> acc
                is Cons -> toList(acc.cons(stream.hd()), stream.tl())
            }

        fun <T> cons(hd: Deferred<T>, tl: Deferred<Stream<T>>): Stream<T> = Cons(hd, tl)

        fun from(i: Int): Stream<Int> = iterate(i) { it + 1 }

        fun <T> iterate(seed: T, f: (T) -> T): Stream<T> =
            cons(Deferred { seed }, Deferred { iterate(f(seed), f) })

        fun <T> iterate(seed: Deferred<T>, f: (T) -> T): Stream<T> =
            cons(seed, Deferred { iterate(f(seed()), f) })

        operator fun <T> invoke(): Stream<T> = Empty
    }
}

fun main() {

    Stream.from(0)
        .takeWhile { it < 4 }
        .toList()
        .let { println(it) }
}
