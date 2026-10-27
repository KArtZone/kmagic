package pro.artkart.algs.lafore.array.stackqueue

class ArrayQueue(
    private val capacity: Int
) {

    private val queue = IntArray(capacity)
    private var head = 0
    private var tail = 0

    fun insert(item: Int) {
        check(!isFull()) { QUEUE_IS_FULL }
        if (tail == capacity)
            tail = 0
        queue[tail++] = item
    }

    fun remove(): Int {
        check(!isEmpty()) { QUEUE_IS_EMPTY }
        if (head == capacity)
            head = 0
        return queue[head++]
    }

    fun peek(): Int = queue[head]

    fun isFull(): Boolean = tail == head - 1 || (tail == capacity && head == 0)

    fun isEmpty(): Boolean = head == tail

    override fun toString(): String = "Queue(${queue.contentToString()})"

    companion object {
        const val QUEUE_IS_EMPTY = "Queue is empty"
        const val QUEUE_IS_FULL = "Queue is full"
    }
}
