package pro.artkart.kmagic.function

import java.math.BigDecimal

tailrec fun add(a: Int, b: Int): Int {
    fun inc(n: Int) = n + 1
    fun dec(n: Int) = n - 1

    return if (b == 0)
        a
    else
        add(inc(a), dec(b))
}

fun factorial(n: Int): Int {
    return if (n == 1)
        1
    else
        n * factorial(n - 1)
}

val factorialValue: (Int) -> Int = { n ->
    if (n == 1)
        1
    else
        n * factorialValue(n - 1)
}

fun fib(n: Int): Int {
    return if (n <= 1)
        1
    else
        fib(n - 1) + fib(n - 2)
}

fun fibonacci(n: Int): BigDecimal {
    tailrec fun fibonacci(current: BigDecimal, previous: BigDecimal, index: Int): BigDecimal {
        return if (index <= 1)
            current
        else
            fibonacci(current + previous, current, index - 1)
    }
    return fibonacci(BigDecimal.ONE, BigDecimal.ONE, n)
}

fun <T> makeString(list: List<T>, delimiter: String): String =
    when {
        list.isEmpty() -> ""
        list.drop(1).isEmpty() -> "${list.first()}${makeString(list.drop(1), delimiter)}"
        else -> "${list.first()}$delimiter${makeString(list.drop(1), delimiter)}"
    }

fun <T> makeStringV2(list: List<T>, delimiter: String): String {
    tailrec fun makeStringV2(acc: String, list: List<T>): String =
        when {
            list.isEmpty() -> acc
            acc.isEmpty() -> makeStringV2("${list.first()}", list.drop(1))
            else -> makeStringV2("$acc$delimiter${list.first()}", list.drop(1))
        }
    return makeStringV2("", list)
}

tailrec fun <T, A> foldLeft(list: List<T>, acc: A, transform: (A, T) -> A): A =
    if (list.isEmpty())
        acc
    else
        foldLeft(list.drop(1), transform(acc, list.first()), transform)

fun listSum(list: List<Int>): Int = foldLeft(list, 0) { acc, item ->
    acc + item
}

fun <T> makeStringV3(list: List<T>, delimiter: String): String = foldLeft(list, "") { acc, item ->
    if (acc.isEmpty())
        "$item"
    else
        "$acc$delimiter$item"
}

fun string(list: List<Char>): String = foldLeft(list, "", String::plus)

fun <T, A> foldRight(list: List<T>, acc: A, transform: (T, A) -> A): A =
    if (list.isEmpty())
        acc
    else
        transform(list.first(), foldRight(list.drop(1), acc, transform))

fun stringV2(list: List<Char>): String = foldRight(list, "") { item, acc ->
    "$item$acc"
}

fun <T> reverse(list: List<T>): List<T> = foldRight(list, listOf()) { item, acc -> acc + item }

fun <T> reverseV2(list: List<T>): List<T> = foldLeft(list, listOf()) { acc, item -> listOf(item) + acc }

fun range(start: Int, end: Int): List<Int> {
    var current = start
    val list = mutableListOf<Int>()
    while (current < end) {
        list += current++
    }
    return list
}

fun <T> unfold(seed: T, f: (T) -> T, p: (T) -> Boolean): List<T> {
    var current = seed
    val list = mutableListOf<T>()

    while (p(current)) {
        list += current
        current = f(current)
    }
    return list
}

fun rangeV2(start: Int, end: Int): List<Int> = unfold(start, { it + 1 }) { it < end }

fun rangeV3(start: Int, end: Int): List<Int> {
    tailrec fun rangeV3(acc: List<Int>, current: Int): List<Int> =
        if (current < end)
            rangeV3(acc + current, current + 1)
        else
            acc
    return rangeV3(listOf(), start)
}

fun <T> unfoldV2(seed: T, f: (T) -> T, p: (T) -> Boolean): List<T> {
    tailrec fun unfoldV2(acc: List<T>, current: T): List<T> =
        if (p(current))
            unfoldV2(acc + current, f(current))
        else
            acc
    return unfoldV2(listOf(), seed)
}

fun rangeV4(start: Int, end: Int): List<Int> = unfoldV2(start, { it + 1 }) { it < end }
