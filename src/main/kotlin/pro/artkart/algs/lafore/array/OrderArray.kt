package pro.artkart.algs.lafore.array

class OrderArray(capacity: Int) : AbstractArray(capacity) {

    override fun minus(item: Int): Boolean = find(item)
        .let { index ->
            when {
                index == capacity -> false
                else -> {
                    compressFrom(index)
                    true
                }
            }
        }

    fun find(item: Int): Int {
        var leftBound = 0
        var rightBound = if (next > 0) next - 1 else 0
        var index: Int
        while (true) {
            if (rightBound < leftBound) {
                return capacity
            }
            index = (leftBound + rightBound) / 2
            val currentItem = array[index]
            when {
                item == currentItem -> return index
                item < currentItem -> rightBound = index - 1
                else -> leftBound = index + 1
            }
        }
    }
}
