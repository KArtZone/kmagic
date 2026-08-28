package pro.artkart.algs.twopointers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAllInAnyOrder
import io.kotest.matchers.shouldBe


class TwoPointersTest : StringSpec({

    "reverseString() should return ['o', 'l', 'l', 'e', 'h']" {
        // given
        val charArray = charArrayOf('h', 'e', 'l', 'l', 'o')
        // when
        charArray.reverseString()
        // then
        charArray shouldBe charArrayOf('o', 'l', 'l', 'e', 'h')
    }

    "isPalindrome() should return true" {
        // given
        val s = "A man, a plan, a canal: Panama"
        // when
        val result = s.isPalindrome()
        // then
        result shouldBe true
    }

    "twoSum(9) should return [2, 7]" {
        // given
        val numbers = listOf(2, 7, 11, 15)
        // when
        val result = numbers.twoSum(9)
        // then
        result shouldBe intArrayOf(2, 7)
    }

    "threeSum() should return [[-1, -1, 2], [-1, 0, 1]]" {
        // given
        val numbers = listOf(-1, 0, 1, 2, -1, -4)
        // when
        val result = numbers.threeSum().map { it.sorted() }
        // then
        result shouldContainAllInAnyOrder listOf(
            listOf(-1, -1, 2), listOf(-1, 0, 1)
        )
    }

    "sortedSquares() should return [0, 1, 9, 16, 100]" {
        // given
        val numbers = listOf(-4, -1, 0, 3, 10)
        // when
        val result = numbers.sortedSquares()
        // then
        result shouldBe listOf(0, 1, 9, 16, 100)
    }

    "heights.maxArea() should return 49" {
        // given
        val heights = listOf(1, 8, 6, 2, 5, 4, 8, 3, 7)
        // when
        val result = heights.maxArea()
        // then
        result shouldBe 49
    }

    "deduplicate() should return [0, 1, 2, 3, 4]" {
        // given
        val numbers = listOf(0, 0, 1, 1, 1, 2, 2, 3, 3, 4)
        // when
        val result = numbers.deduplicate()
        // then
        result shouldBe listOf(0, 1, 2, 3, 4)
    }

    "removeDuplicates() should deduplicate array and return 5" {
        // given
        val numbers = intArrayOf(0, 0, 1, 1, 1, 2, 2, 3, 3, 4)
        // when
        val result = numbers.removeDuplicates()
        // then
        numbers.dropLast(result) shouldBe intArrayOf(0, 1, 2, 3, 4)
        result shouldBe 5
    }

    "moveZeros() should move zeros to right" {
        // given
        val numbers = intArrayOf(0, 1, 0, 3, 12)
        // when
        numbers.moveZeros()
        // then
        numbers shouldBe intArrayOf(1, 3, 12, 0, 0)
    }

    "abbdefghkpy.isSubSequence(behy)" {
        // given
        val string = "abbdefghkpy"
        // when
        val result = string.isSubsequence("behy")
        // then
        result shouldBe true
    }

    "backspaceCompare(ab#c, ad#c) should return true" {
        // given
        val s1 = "abb##"
        val s2 = "bb##a"
        // when
        val result = backspaceCompare(s1, s2)
        // then
        result shouldBe true
    }

    "listMerge(left, right) should return [1, 2, 2, 3, 5, 7]" {
        // given
        val left = listOf(1, 3, 5)
        val right = listOf(2, 2, 7)
        // when
        val result = listMerge(left, right)
        // then
        result shouldBe listOf(1, 2, 2, 3, 5, 7)
    }
})
