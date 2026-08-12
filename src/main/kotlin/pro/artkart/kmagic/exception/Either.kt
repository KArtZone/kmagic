package pro.artkart.kmagic.exception

sealed class Either<E, out T> {

    internal data class Left<E, out T>(
        private val value: E
    ) : Either<E, T>() {
        override fun toString(): String = "Left($value)"
        override fun <R> map(transform: (T) -> R): Either<E, R> = Left(value)
        override fun <R> flatMap(transform: (T) -> Either<E, R>): Either<E, R> = Left(value)
    }

    internal data class Right<E, out T>(
        internal val value: T
    ) : Either<E, T>() {
        override fun toString(): String = "Right($value)"
        override fun <R> map(transform: (T) -> R): Either<E, R> = Right(transform(value))
        override fun <R> flatMap(transform: (T) -> Either<E, R>): Either<E, R> = transform(value)
    }

    abstract fun <R> map(transform: (T) -> R): Either<E, R>

    abstract fun <R> flatMap(transform: (T) -> Either<E, R>): Either<E, R>

    fun getOrElse(default: () -> @UnsafeVariance T): T = when (this) {
        is Left -> default()
        is Right -> value
    }

    fun orElse(default: () -> Either<E, @UnsafeVariance T>): Either<E, T> = map { this }
        .getOrElse(default)

    companion object {

        fun <E, T> left(value: E): Either<E, T> = Left(value)

        fun <E, T> right(value: T): Either<E, T> = Right(value)
    }
}
