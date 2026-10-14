package pro.artkart.algs.lafore.stackqueue

class ArrayPriorityQueue(
    private val capacity: Int
) {

    private val queue = IntArray(capacity)
    private var next = 0

    fun insert(item: Int) {
        check(!isFull()) { QUEUE_IS_FULL }
        if (isEmpty())
            queue[next] = item
        else {
            var index = next - 1
            while (index >= 0 && item > queue[index]) {
                queue[index + 1] = queue[index--]
            }
            queue[index + 1] = item
        }
        ++next
    }

    fun remove(): Int {
        check(!isEmpty()) { QUEUE_IS_EMPTY }
        return queue[--next]
    }

    fun peek(): Int {
        check(!isEmpty()) { QUEUE_IS_EMPTY }
        return queue[next - 1]
    }

    fun isEmpty(): Boolean = next == 0

    fun isFull(): Boolean = next == capacity

    override fun toString(): String = "APQ(${queue.contentToString()})"

    companion object {
        const val QUEUE_IS_EMPTY = "Queue is empty"
        const val QUEUE_IS_FULL = "Queue is full"
    }
}
