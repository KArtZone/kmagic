package pro.artkart.kmagic.exception

import java.io.Serializable

sealed class Resolution<out T> : Serializable {

    internal class Failure<out T>(
        internal val exception: RuntimeException
    ) : Resolution<T>() {
        override fun toString(): String = "Failure(${exception.message})"
        override fun <R> map(transform: (T) -> R): Resolution<R> = Failure(exception)
        override fun <R> flatMap(transform: (T) -> Resolution<R>): Resolution<R> = Failure(exception)
        override fun mapFailure(error: String): Resolution<T> = failure(RuntimeException(error, exception))
        override fun forEach(
            onSuccess: (T) -> Unit,
            onFailure: (RuntimeException) -> Unit,
            onEmpty: () -> Unit
        ) = onFailure(exception)
    }

    internal object Empty : Resolution<Nothing>() {
        override fun toString(): String = "Empty"
        override fun <R> map(transform: (Nothing) -> R): Resolution<R> = Empty
        override fun <R> flatMap(transform: (Nothing) -> Resolution<R>): Resolution<R> = Empty
        override fun mapFailure(error: String): Resolution<Nothing> = Empty
        override fun forEach(
            onSuccess: (Nothing) -> Unit,
            onFailure: (RuntimeException) -> Unit,
            onEmpty: () -> Unit
        ) = onEmpty()
    }

    internal class Success<out T>(
        internal val value: T
    ) : Resolution<T>() {

        override fun toString(): String = "Success($value)"

        override fun <R> map(transform: (T) -> R): Resolution<R> = try {
            Success(transform(value))
        } catch (e: RuntimeException) {
            Failure(e)
        } catch (e: Exception) {
            Failure(RuntimeException(e))
        }

        override fun <R> flatMap(transform: (T) -> Resolution<R>): Resolution<R> = try {
            transform(value)
        } catch (e: RuntimeException) {
            Failure(e)
        } catch (e: Exception) {
            Failure(RuntimeException(e))
        }

        override fun mapFailure(error: String): Resolution<T> = this

        override fun forEach(
            onSuccess: (T) -> Unit,
            onFailure: (RuntimeException) -> Unit,
            onEmpty: () -> Unit
        ) = onSuccess(value)
    }

    abstract fun <R> map(transform: (T) -> R): Resolution<R>

    abstract fun <R> flatMap(transform: (T) -> Resolution<R>): Resolution<R>

    abstract fun mapFailure(error: String): Resolution<T>

    abstract fun forEach(
        onSuccess: (T) -> Unit = {},
        onFailure: (RuntimeException) -> Unit = {},
        onEmpty: () -> Unit = {}
    )

    fun getOrElse(default: @UnsafeVariance T): T = when (this) {
        is Success -> value
        else -> default
    }

    fun getOrElse(default: () -> @UnsafeVariance T): T = when (this) {
        is Success -> value
        else -> default()
    }

    fun orElse(default: () -> Resolution<@UnsafeVariance T>): Resolution<T> = when (this) {
        is Success -> this
        else -> try {
            default()
        } catch (e: RuntimeException) {
            Failure(e)
        } catch (e: Exception) {
            Failure(RuntimeException(e))
        }
    }

    fun filter(p: (T) -> Boolean): Resolution<T> =
        flatMap { if (p(it)) this else failure("Condition not matched") }

    fun filter(p: (T) -> Boolean, error: String): Resolution<T> = flatMap { if (p(it)) this else failure(error) }

    fun exists(p: (T) -> Boolean): Boolean = map(p).getOrElse(false)

    companion object {

        operator fun <T> invoke(value: T? = null): Resolution<T> = if (value != null)
            Success(value)
        else
            Failure(NullPointerException())

        operator fun <T> invoke(value: T? = null, message: String): Resolution<T> = if (value != null)
            Success(value)
        else
            Failure(NullPointerException(message))

        operator fun <T> invoke(value: T? = null, p: (T) -> Boolean): Resolution<T> = when {
            value == null -> Failure(NullPointerException())
            else -> when {
                p(value) -> Success(value)
                else -> Empty
            }
        }

        operator fun <T> invoke(value: T? = null, message: String, p: (T) -> Boolean): Resolution<T> = when {
            value == null -> Failure(NullPointerException())
            else -> when {
                p(value) -> Success(value)
                else -> failure(
                    IllegalArgumentException("Argument $value does not match condition: $message")
                )
            }
        }

        fun <T> failure(message: String): Resolution<T> =
            Failure(IllegalStateException(message))

        fun <T> failure(exception: RuntimeException): Resolution<T> =
            Failure(exception)

        fun <T> failure(exception: Exception): Resolution<T> =
            Failure(IllegalStateException(exception))
    }
}

fun <T, R> lift(f: (T) -> R): (Resolution<T>) -> Resolution<R> = { it.map(f) }

fun <T, U, R> lift2(f: (T) -> (U) -> R): (Resolution<T>) -> (Resolution<U>) -> Resolution<R> = { a ->
    { b ->
        a.map(f).flatMap { b.map(it) }
    }
}

fun <T, U, R> lift2V2(f: (T) -> (U) -> R): (Resolution<T>) -> (Resolution<U>) -> Resolution<R> = { a ->
    { b ->
        a.flatMap { av ->
            b.map { bv ->
                f(av)(bv)
            }
        }
    }
}

fun <A, B, C, D> lift3(f: (A) -> (B) -> (C) -> D):
            (Resolution<A>) -> (Resolution<B>) -> (Resolution<C>) -> Resolution<D> = { a ->
    { b ->
        { c ->
            a.map(f)
                .flatMap { bcd ->
                    b.map(bcd)
                        .flatMap { cd ->
                            c.map(cd)
                        }
                }
        }
    }
}

fun <A, B, C> map2(r1: Resolution<A>, r2: Resolution<B>, f: (A) -> (B) -> C): Resolution<C> =
    r1.flatMap { a ->
        r2.map { b ->
            f(a)(b)
        }
    }

fun <A, B, C> map2V2(r1: Resolution<A>, r2: Resolution<B>, f: (A) -> (B) -> C): Resolution<C> =
    lift2(f)(r1)(r2)
