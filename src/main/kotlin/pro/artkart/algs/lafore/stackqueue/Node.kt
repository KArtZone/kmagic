package pro.artkart.algs.lafore.stackqueue

import pro.artkart.kmagic.common.Option

const val EMPTY = "E"

class Node<T>(
    val value: T,
    var next: Option<Node<T>> = Option.None
) {
    override fun toString(): String = "$value -> ${
        next.map { "$it" }
            .getOrElse { EMPTY }
    }"
}
