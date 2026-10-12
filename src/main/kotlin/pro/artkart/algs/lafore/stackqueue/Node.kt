package pro.artkart.algs.lafore.stackqueue

import pro.artkart.kmagic.common.Option
import pro.artkart.kmagic.common.Option.None

const val EMPTY = "E"

class OneWayNode<T>(
    val value: T,
    var next: Option<OneWayNode<T>> = None
) {
    override fun toString(): String = "$value${
        next.map { " -> $it" }
            .getOrElse { "" }
    }"
}

class TwoWayNode<T>(
    val value: T,
    var next: Option<TwoWayNode<T>> = None,
    var previous: Option<TwoWayNode<T>> = None
) {

    override fun toString(): String = "$value${
        next.map { " <-> $it" }.getOrElse { "" }
    }"
}
