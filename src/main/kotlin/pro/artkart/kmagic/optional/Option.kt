package pro.artkart.kmagic.optional

sealed class Option<out T> {

    internal object None : Option<Nothing>() {
        override fun isEmpty(): Boolean = true
        override fun toString(): String = "None"
        override fun equals(other: Any?): Boolean = other === None
        override fun hashCode(): Int = 0
    }

    internal data class Some<T>(
        val value: T
    ) : Option<T>() {
        override fun isEmpty(): Boolean = false
    }

    abstract fun isEmpty(): Boolean

    fun getOrElse(default: () -> @UnsafeVariance T) = when (this) {
        None -> default()
        is Some -> value
    }

    fun orElse(default: () -> Option<@UnsafeVariance T>): Option<T> = map { this }.getOrElse { default() }

    fun <R> map(transform: (T) -> R): Option<R> = when (this) {
        None -> None
        is Some -> Some(transform(value))
    }

    fun <R> flatMap(transform: (T) -> Option<R>): Option<R> = when (this) {
        None -> None
        is Some -> transform(value)
    }

    fun <R> flatMapV2(transform: (T) -> Option<R>): Option<R> = map(transform).getOrElse { None }

    fun filter(p: (T) -> Boolean): Option<T> = flatMap { if (p(it)) this else None }

    companion object {

        operator fun <T> invoke(value: T? = null): Option<T> = when (value) {
            null -> None
            else -> Some(value)
        }
    }
}
