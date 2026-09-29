package pro.artkart.kmagic.io

import io.github.oshai.kotlinlogging.KotlinLogging
import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.io.IO.Companion.forever
import pro.artkart.kmagic.io.IO.Companion.repeat
import pro.artkart.kmagic.io.IO.Console
import pro.artkart.kmagic.lazy.Deferred
import pro.artkart.kmagic.lazy.Stream
import pro.artkart.kmagic.list.ImmutableList
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.System.`in`

val log = KotlinLogging.logger { }

class IO<out T>(
    private val f: () -> T
) {

    fun <R> map(transform: (T) -> R): IO<R> = IO { transform(f()) }

    fun <R> flatMap(transform: (T) -> IO<R>): IO<R> = IO { transform(f())() }

    operator fun plus(other: IO<@UnsafeVariance T>): IO<T> = IO {
        f()
        other.f()
    }

    operator fun invoke() = f()

    companion object {

        fun <T, U, R> map2(first: IO<T>, second: IO<U>, f: (T) -> (U) -> R): IO<R> =
            first.flatMap { firstIO ->
                second.map { secondIO ->
                    f(firstIO)(secondIO)
                }
            }

        fun <T, R> IO<T>.forever(): IO<R> = flatMap { forever() }

        fun <T> IO<T>.repeat(n: Int): IO<ImmutableList<T>> = Stream.fill(n, Deferred { this })
            .foldRight(Deferred { IO { ImmutableList() } }) { item ->
                { acc ->
                    map2(item, acc()) { i ->
                        { list ->
                            list.cons(i)
                        }
                    }
                }
            }

        val empty: IO<Unit> = IO {}

        operator fun <T> invoke(type: T): IO<T> = IO { type }
    }

    object Console {

        val reader = BufferedReader(InputStreamReader(`in`))

        fun readln(): IO<String> = IO {
            try {
                reader.readLine()
            } catch (e: IOException) {
                throw IllegalStateException(e)
            }
        }

        fun print(obj: Any): IO<Unit> = IO { kotlin.io.print(obj) }

        fun println(obj: Any): IO<Unit> = IO { kotlin.io.println(obj) }
    }
}


object IoTest {

    fun getName() = "Mikey"

    fun buildMessage(name: String): String = "Hello, $name!"

    fun test1() {
        val program = show(toString(inverse(3)))
        program()
    }

    fun test2() {
        val instruction1 = IO { print("Hello, ") }
        val instruction2 = IO { print(getName()) }
        val instruction3 = IO { println("!") }

        val script = instruction1 + instruction2 + instruction3
        script()
    }

    fun test3() {
        val program = ImmutableList(
            IO { print("Hello, ") },
            IO { print(getName()) },
            IO { println("!") }
        ).foldRight(IO.empty) { a -> { b -> a + b } }
        program()
    }

    fun test4() {
        val script = Console.println("Enter your name: ")
            .map { Console.readln()() }
            .map { buildMessage(it) }
            .map { Console.println(it)() }
        script()
    }

    fun test5() {
        fun buildMessage(name: String): String = "Hello, $name!"
        val script = Console.println("Enter your name: ")
            .flatMap { Console.readln() }
            .map { buildMessage(it) }
            .flatMap { Console.println(it) }
        script()
    }

    fun test6() {
        val program = IO { Console.println("Hooray! ") }.repeat(3)
        program().forEach { it() }
    }

    fun test7() {
        IO { "I love you" }
            .forever<String, String>()
            .flatMap(Console::println)()
    }
}

fun show(message: String) = IO { log.info { message } }

fun <T> toString(resolution: Resolution<T>): String =
    resolution.map { it.toString() }.getOrElse { resolution.toString() }

fun inverse(value: Int): Resolution<Double> = when (value) {
    0 -> Resolution.failure("Division by 0")
    else -> Resolution(1.0 / value)
}

fun main() {

    with(IoTest) {
        test1()
        test2()
        test3()
        test4()
        test5()
        test6()
        test7()
    }
}
