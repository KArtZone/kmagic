package pro.artkart.kmagic.utils

import pro.artkart.kmagic.list.ImmutableList
import pro.artkart.kmagic.list.sum
import pro.artkart.kmagic.optional.Option
import kotlin.math.pow

fun mean(list: ImmutableList<Double>): Option<Double> = when {
    list.isEmpty() -> Option()
    else -> Option(list.sum() / list.size())
}

val variance: (ImmutableList<Double>) -> Option<Double> = { list ->
    mean(list).flatMap { mean ->
        mean(list.map { item -> (item - mean).pow(2) })
    }
}

fun <T, R> lift(block: (T) -> R): (Option<T>) -> Option<R> = { it.map(block) }

fun <T, R> liftWithException(block: (T) -> R): (Option<T>) -> Option<R> = {
    try {
        it.map(block)
    } catch (_: Exception) {
        Option()
    }
}

fun <T, U, R> map2(left: Option<T>, right: Option<U>, block: (T, U) -> R): Option<R> =
    left.flatMap { left ->
        right.map {
            block(left, it)
        }
    }

fun <T> sequence(list: ImmutableList<Option<T>>): Option<ImmutableList<T>> = when (list) {
    ImmutableList.Nil -> Option(ImmutableList())
    is ImmutableList.Cons -> list.head.flatMap { item ->
        sequence(list.tail).map { acc -> acc.cons(item) }
    }
}

fun <T> sequence1(list: ImmutableList<Option<T>>): Option<ImmutableList<T>> =
    list.foldRightV2(Option(ImmutableList())) { item ->
        { acc ->
            map2(acc, item) { list, item ->
                list.cons(item)
            }
        }
    }

fun <T, R> traverse(list: ImmutableList<T>, transform: (T) -> Option<R>): Option<ImmutableList<R>> =
    list.coFoldRight(Option(ImmutableList())) { item ->
        { acc ->
            map2(acc, transform(item)) { list, i ->
                list.cons(i)
            }
        }
    }

fun <T> sequenceV2(list: ImmutableList<Option<T>>): Option<ImmutableList<T>> = traverse(list) { it }
