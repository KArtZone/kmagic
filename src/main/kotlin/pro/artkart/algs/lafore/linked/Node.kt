package pro.artkart.algs.lafore.linked

class Node<T>(
    val value: T,
    var next: Node<T>? = null
) {

    override fun toString(): String = "(Node=$value) -> ${next.toString()}"
}
