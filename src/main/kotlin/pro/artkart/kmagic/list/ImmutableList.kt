package pro.artkart.kmagic.list

sealed class ImmutableList<T> {

    private object Nil : ImmutableList<Nothing>() {

        override fun isEmpty(): Boolean = true

        override fun toString(): String = "[Nil]"
    }

    private class Cons<T>(
        val head: T,
        val tail: ImmutableList<T>
    ) : ImmutableList<T>() {

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "[${toString("", this)}Nil]"

        private tailrec fun toString(acc: String, list: ImmutableList<T>): String =
            when (list) {
                Nil -> acc
                is Cons -> toString("$acc${list.head}, ", list.tail)
            }
    }

    abstract fun isEmpty(): Boolean

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

    companion object {

        tailrec fun <T> concatLeft(left: ImmutableList<T>, right: ImmutableList<T>): ImmutableList<T> = when (left) {
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
                Nil as ImmutableList<T>
            else
                initRight(list.tail).cons(list.head)
        }

        operator fun <T> invoke(vararg items: T): ImmutableList<T> =
            items.foldRight(Nil as ImmutableList<T>) { item, acc -> Cons(item, acc) }
    }
}
