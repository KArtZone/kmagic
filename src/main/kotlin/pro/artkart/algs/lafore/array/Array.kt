package pro.artkart.algs.lafore.array

import kotlin.collections.get
import kotlin.collections.set
import kotlin.compareTo
import kotlin.text.set

abstract class Array(
    protected val capacity: Int
) {

    protected val array = Array(capacity) { 0 }

    protected var next = 0

    operator fun plus(item: Int) = when {
        capacity > next -> array[next++] = item
        else -> throw ArrayIsFull()
    }

    abstract operator fun minus(item: Int): Boolean

    fun bubbleSort() {
        (next - 1 downTo 1).forEach { end ->
            (0 until end).forEach { index ->
                if (array[index] > array[index + 1])
                    swap(index, index + 1)
            }
        }
    }

    fun selectSort() {
        (0 until next - 1).forEach { mainIndex ->
            var min = mainIndex
            (mainIndex + 1 until next).forEach { index ->
                if (array[index] < array[min])
                    min = index
            }
            swap(mainIndex, min)
        }
    }

    fun insertSort() {
        (1 until next).forEach { mainIndex ->
            val marked = array[mainIndex]
            var moveIndex = mainIndex
            while (moveIndex > 0 && array[moveIndex - 1] >= marked) {
                array[moveIndex] = array[moveIndex-- - 1]
            }
            array[moveIndex] = marked
        }
    }

    override fun toString(): String = "Array(${array.sliceArray(0 until next).contentDeepToString()})"

    protected fun compressFrom(searchIndex: Int) {
        (searchIndex until next - 1).forEach { index ->
            array[index] = array[index + 1]
        }
        --next
    }

    private fun swap(from: Int, to: Int) {
        val tmp = array[from]
        array[from] = array[to]
        array[to] = tmp
    }

    companion object {

        operator fun invoke(vararg items: Int, ordered: Boolean = false) =
            when {
                ordered -> OrderArray(items.size)
                else -> HighArray(items.size)
            }.apply {
                items.forEach { this + it }
            }

    }
}

class ArrayIsFull : RuntimeException("Array is Full")
