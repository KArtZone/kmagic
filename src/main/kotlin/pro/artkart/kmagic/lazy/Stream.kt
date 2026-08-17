package pro.artkart.kmagic.lazy

import pro.artkart.kmagic.exception.Resolution

sealed class Stream<out T> {

    abstract fun isEmpty(): Boolean

    abstract fun head(): Resolution<T>

    abstract fun tail(): Resolution<Stream<T>>

    private object Empty : Stream<Nothing>() {
        override fun isEmpty(): Boolean = true
        override fun head(): Resolution<Nothing> = Resolution()
        override fun tail(): Resolution<Stream<Nothing>> = Resolution()
    }

    private class Cons<T>(
        internal val hd: Deferred<T>,
        internal val tl: Deferred<Stream<T>>
    ) : Stream<T>() {

        override fun isEmpty(): Boolean = false

        override fun head(): Resolution<T> = Resolution(hd())

        override fun tail(): Resolution<Stream<T>> = Resolution(tl())
    }

    companion object {

        fun <T> cons(hd: Deferred<T>, tl: Deferred<Stream<T>>): Stream<T> = Cons(hd, tl)

        fun from(i: Int): Stream<Int> = cons(Deferred { i }, Deferred { from(i + 1) })

        operator fun <T> invoke(): Stream<T> = Empty
    }
}
