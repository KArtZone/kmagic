package pro.artkart.algs.twopointers

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


fun CharArray.reverseString() {
    var left = 0
    var right = size - 1
    fun swap(left: Int, right: Int) {
        val tmp = this[left]
        this[left] = this[right]
        this[right] = tmp
    }
    while (left < right) {
        swap(left++, right--)
    }
}

fun String.isPalindrome(): Boolean {
    var left = 0
    var right = length - 1

    while (left < right) {
        val l = this[left]
        val r = this[right]
        when {
            !l.isLetterOrDigit() -> ++left
            !r.isLetterOrDigit() -> --right
            else -> {
                if (l.uppercaseChar() != r.uppercaseChar())
                    return false
                ++left
                --right
            }
        }
    }
    return true
}

fun List<Int>.twoSum(target: Int): IntArray {
    var l = 0
    var r = size - 1
    while (l < r) {
        val left = this[l]
        val right = this[r]
        when {
            left + right < target -> ++l
            left + right > target -> --r
            else -> return intArrayOf(left, right)
        }
    }
    return intArrayOf()
}

fun List<Int>.threeSum(): List<List<Int>> {
    val numbers = this.sorted()
    val result = mutableListOf<List<Int>>()
    numbers.forEachIndexed { index, target ->
        var l = index + 1
        var r = numbers.size - 1
        while (l < r) {
            val left = numbers[l]
            val right = numbers[r]
            when {
                left + right > -target -> --r
                left + right < -target -> ++l
                else -> {
                    result += listOf(target, left, right)
                    ++l
                    --r
                }
            }
        }
    }
    return result
}

fun List<Int>.sortedSquares(): List<Int> {
    val result = mutableListOf<Int>()
    var l = 0
    var r = size - 1
    while (l <= r) {
        val left = this[l]
        val right = this[r]
        when {
            abs(left) > abs(right) -> {
                result.addFirst(left * left)
                ++l
            }

            else -> {
                result.addFirst(right * right)
                --r
            }
        }
    }
    return result
}

fun List<Int>.maxArea(): Int {
    var result = 0
    var l = 0
    var r = size - 1
    while (l < r) {
        val left = this[l]
        val right = this[r]
        val square = min(left, right) * (r - l)
        result = max(result, square)
        when {
            left < right -> ++l
            else -> --r
        }
    }
    return result
}

fun List<Int>.deduplicate(): List<Int> {
    val result = mutableListOf<Int>()
    var current = this[0]
    forEach { item ->
        when {
            item == current -> {}
            else -> {
                result += current
                current = item
            }
        }
    }
    return result + current
}

fun IntArray.removeDuplicates(): Int {
    var index = 0
    forEach { item ->
        if (item != this[index]) {
            this[++index] = item
        }
    }
    return index + 1
}

fun IntArray.moveZeros() {
    fun swap(left: Int, right: Int) {
        val tmp = this[left]
        this[left] = this[right]
        this[right] = tmp
    }

    var write = 0
    forEachIndexed { index, item ->
        when {
            item != 0 -> swap(index, write++)
        }
    }
}

fun String.isSubsequence(subSequence: String): Boolean {
    var index = 0
    forEach { char ->
        when {
            index == subSequence.length -> return true
            char == subSequence[index] -> ++index
        }
    }
    return index == subSequence.length
}

fun backspaceCompare(left: String, right: String): Boolean {
    var lCounter = 0
    var rCounter = 0
    var l = left.length - 1
    var r = right.length - 1

    while (l >= 0 || r >= 0) {
        val lChar = if (l >= 0) left[l] else '_'
        val rChar = if (r >= 0) right[r] else '_'
        when {
            lChar == '#' -> {
                ++lCounter
                --l
            }

            rChar == '#' -> {
                ++rCounter
                --r
            }

            else -> when {
                lCounter > 0 -> {
                    --lCounter
                    --l
                }

                rCounter > 0 -> {
                    --rCounter
                    --r
                }

                lChar != rChar -> return false

                else -> {
                    --l
                    --r
                }
            }
        }
    }
    return true
}

fun listMerge(left: List<Int>, right: List<Int>): List<Int> {
    val result = mutableListOf<Int>()
    var l = 0
    var r = 0
    while (l < left.size && r < right.size) {
        result += if (left[l] < right[r])
            left[l++]
        else
            right[r++]
    }
    result += if (l < left.size)
        left.subList(l, left.size)
    else
        right.subList(r, right.size)
    return result
}
