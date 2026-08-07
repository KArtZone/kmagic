package pro.artkart.kmagic.list

sealed class ImmutableList<T> {

    private object Nil : ImmutableList<Nothing>() {

        override fun isEmpty(): Boolean = true

        override fun toString(): String = "[Nil]"
    }

    private class Cons<T>(
        internal val head: T,
        internal val tail: ImmutableList<T>
    ) : ImmutableList<T>() {

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "[${toString("", this)}Nil]"

        private tailrec fun toString(acc: String, list: ImmutableList<T>): String =
            when (list) {
                is Nil -> acc
                is Cons -> toString("$acc${list.head}, ", list.tail)
            }
    }

    abstract fun isEmpty(): Boolean

    fun cons(item: T): ImmutableList<T> = Cons(item, this)

    fun setHead(item: T): ImmutableList<T> = when (this) {
        is Nil -> throw UnsupportedOperationException("Operation not allowed")
        is Cons -> tail.cons(item)
    }

    companion object {
        operator fun <T> invoke(vararg items: T): ImmutableList<T> =
            items.foldRight(Nil as ImmutableList<T>) { item, acc -> Cons(item, acc) }
    }
}
