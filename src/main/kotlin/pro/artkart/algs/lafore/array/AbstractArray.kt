package pro.artkart.algs.lafore.array

import io.github.oshai.kotlinlogging.KotlinLogging

abstract class AbstractArray(
    protected val capacity: Int
) {

    protected val array = Array(capacity) { 0 }

    protected var next = 0

    operator fun plus(item: Int) = when {
        capacity > next -> array[next++] = item
        else -> throw HighArrayFull()
    }

    abstract operator fun minus(item: Int): Boolean

    override fun toString(): String = "HighArray(${array.sliceArray(0 until next).contentDeepToString()})"

    protected fun compressFrom(searchIndex: Int) {
        (searchIndex until next - 1).forEach { index ->
            array[index] = array[index + 1]
        }
        --next
    }
}

class HighArrayFull : RuntimeException("High Array is Full")

fun main() {
    val log = KotlinLogging.logger { }
    val n = 20
    val highArray = HighArray(n)


    log.info { highArray.contains(3) }
    val orderArray = OrderArray(n)

    log.info { orderArray.find(3) }
}
