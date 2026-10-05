package pro.artkart.algs.lafore.array

class HighArray(capacity: Int) : AbstractArray(capacity) {

    override operator fun minus(item: Int): Boolean {
        (0 until next).forEach { index ->
            if (array[index] == item) {
                compressFrom(index)
                return true
            }
        }
        return false
    }

    fun contains(item: Int): Boolean {
        (0 until next).forEach { index ->
            if (array[index] == item)
                return true
        }
        return false
    }
}
