package pro.artkart.algs.lafore.stackqueue

class ArrayStack(
    private val capacity: Int
) {

    private val stack = IntArray(capacity)

    private var head = -1

    fun push(item: Int) {
        check(!isFull()) { "Stack is full" }
        stack[++head] = item
    }

    fun pop(): Int {
        check(!isEmpty()) { "Stack is empty" }
        return stack[head--]
    }

    fun peek(): Int {
        check(!isEmpty()) { "Stack is empty" }
        return stack[head]
    }

    fun isEmpty(): Boolean = head == -1

    fun isFull(): Boolean = head == capacity - 1
}
