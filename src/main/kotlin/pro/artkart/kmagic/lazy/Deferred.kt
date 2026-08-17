package pro.artkart.kmagic.lazy

import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.list.ImmutableList
import pro.artkart.kmagic.utils.traverseResolution

class Deferred<T>(f: () -> T) : () -> T {

    private val value: T by lazy(f)

    fun <R> map(f: (T) -> R): Deferred<R> = Deferred { f(value) }

    fun <R> flatMap(f: (T) -> Deferred<R>): Deferred<R> = Deferred { f(value)() }

    override operator fun invoke(): T = value

    companion object {

        val lift2: ((String) -> (String) -> String) -> (Deferred<String>) -> (Deferred<String>) -> Deferred<String> =
            { f ->
                { first ->
                    { second ->
                        Deferred { f(first())(second()) }
                    }
                }
            }

        fun lift2V2(f: (String) -> (String) -> String): (Deferred<String>) -> (Deferred<String>) -> Deferred<String> =
            { first ->
                { second ->
                    Deferred { f(first())(second()) }
                }
            }

        fun <T, U, R> lift2V3(f: (T) -> (U) -> R): (Deferred<T>) -> (Deferred<U>) -> Deferred<R> = { first ->
            { second ->
                Deferred { f(first())(second()) }
            }
        }
    }
}

fun constructMessage(greetings: Deferred<String>, name: Deferred<String>): Deferred<String> =
    Deferred {
        "${greetings()}, ${name()}!"
    }

val constructMessage: (Deferred<String>) -> (Deferred<String>) -> Deferred<String> =
    { greetings ->
        { name -> Deferred { "${greetings()}, ${name()}!" } }
    }

fun <T> sequence(list: ImmutableList<Deferred<T>>): Deferred<ImmutableList<T>> =
    Deferred { list.map { it() } }

fun <T> sequenceResult(list: ImmutableList<Deferred<T>>): Deferred<Resolution<ImmutableList<T>>> = Deferred {
    try {
        list.foldLeft(Resolution(ImmutableList())) { acc ->
            { item ->
                acc.map { it.cons(item()) }
            }
        }
    } catch (e: Exception) {
        Resolution.failure(RuntimeException(e))
    }
}

fun <T> sequenceResultV2(list: ImmutableList<Deferred<T>>): Deferred<Resolution<ImmutableList<T>>> = Deferred {
    try {
        Resolution(list.map { it() })
    } catch (e: Exception) {
        Resolution.failure(RuntimeException(e))
    }
}

fun <A> sequenceResultV3(lst: ImmutableList<Deferred<A>>): Deferred<Resolution<ImmutableList<A>>> =
    Deferred { traverseResolution(lst) { Resolution(it()) } }
