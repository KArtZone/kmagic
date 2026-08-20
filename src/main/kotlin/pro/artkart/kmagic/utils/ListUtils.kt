package pro.artkart.kmagic.utils

import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.lazy.Deferred
import pro.artkart.kmagic.lazy.Stream
import pro.artkart.kmagic.list.ImmutableList
import pro.artkart.kmagic.list.sum
import pro.artkart.kmagic.optional.Option
import kotlin.math.pow

fun mean(list: ImmutableList<Double>): Option<Double> = when {
    list.isEmpty() -> Option()
    else -> Option(list.sum() / list.size)
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

fun <T> flattenResult(list: ImmutableList<Resolution<T>>): ImmutableList<T> =
    list.flatMap { resolution ->
        resolution.map { ImmutableList(it) }
            .getOrElse { ImmutableList() }
    }

fun <T> flattenResultV3(list: ImmutableList<Resolution<T>>): ImmutableList<T> =
    list.filter { it is Resolution.Success }
        .map { (it as Resolution.Success).value }

fun <T> flattenResultV2(list: ImmutableList<Resolution<T>>): ImmutableList<T> =
    list.coFoldRight(ImmutableList()) { item ->
        { acc ->
            when (item) {
                is Resolution.Success -> acc.cons(item.value)
                else -> acc
            }
        }
    }

fun <T> sequence(list: ImmutableList<Resolution<T>>): Resolution<ImmutableList<T>> =
    list.coFoldRight(Resolution(ImmutableList())) { item ->
        { acc ->
            acc.flatMap { list ->
                item.map { list.cons(it) }
            }
        }
    }

fun <T, R> traverseResolution(list: ImmutableList<T>, transform: (T) -> Resolution<R>): Resolution<ImmutableList<R>> =
    list.coFoldRight(Resolution(ImmutableList())) { item ->
        { acc ->
            acc.flatMap { list ->
                transform(item).map { list.cons(it) }
            }
        }
    }

fun <T> sequenceV2(list: ImmutableList<Resolution<T>>): Resolution<ImmutableList<T>> =
    traverseResolution(list) { it }

fun <T, U, R> zip(left: ImmutableList<T>, right: ImmutableList<U>, f: (T) -> (U) -> R): ImmutableList<R> {
    fun zip(acc: ImmutableList<R>, left: ImmutableList<T>, right: ImmutableList<U>): ImmutableList<R> =
        when {
            left is ImmutableList.Cons && right is ImmutableList.Cons -> zip(
                acc.cons(f(left.head)(right.head)),
                left.tail,
                right.tail
            )

            else -> acc.reverseV2()
        }
    return zip(ImmutableList(), left, right)
}

fun <T, U, R> product(left: ImmutableList<T>, right: ImmutableList<U>, f: (T) -> (U) -> R): ImmutableList<R> =
    left.flatMap { leftItem ->
        right.map { rightItem ->
            f(leftItem)(rightItem)
        }
    }

fun <T, U> unzip(list: ImmutableList<Pair<T, U>>): Pair<ImmutableList<T>, ImmutableList<U>> =
    list.unzip { it }

fun <S, T> unfold(seed: S, f: (S) -> Option<Pair<T, S>>): ImmutableList<T> {
    tailrec fun unfold(acc: ImmutableList<T>, current: Option<Pair<T, S>>): ImmutableList<T> = when (current) {
        Option.None -> acc
        is Option.Some -> unfold(acc.cons(current.value.first), f(current.value.second))
    }
    return unfold(ImmutableList(), f(seed)).reverseV2()
}

fun <S, T> unfoldV2(seed: S, f: (S) -> Option<Pair<T, S>>): ImmutableList<T> =
    f(seed).map<ImmutableList<T>> { unfoldV2(it.second, f).cons(it.first) }
        .getOrElse { ImmutableList() }

fun range(start: Int, end: Int): ImmutableList<Int> = unfoldV2(start) {
    if (it < end)
        Option(Pair(it, it + 1))
    else
        Option()
}

fun <T> ImmutableList<T>.stream(): Stream<T> = foldRight(Deferred { Stream.Empty as Stream<T> }) { item ->
    { acc ->
        Deferred { Stream.cons(Deferred { item }, acc) }
    }
}()

fun String.toImmutableList(): ImmutableList<Char> = this.toCharArray()
    .foldRight(ImmutableList()) { char, acc ->
        acc.cons(char)
    }
