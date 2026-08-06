package pro.artkart.kmagic.function

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class RecursionTest : StringSpec({

    "add(2, 5) should return 7" {
        add(2, 5) shouldBe 7
    }

    "factorial(5) should return 120" {
        factorial(5) shouldBe 120
    }

    "factorialValue(5) should return 120" {
        factorialValue(5) shouldBe 120
    }

    "fib(7) should return 21" {
        fib(7) shouldBe 21
    }

    "fibonacci(20) should return 10946" {
        fibonacci(20) shouldBe BigDecimal(10946)
    }

    "makeString(listOf(1, 2, 3), \":\" should return 1:2:3" {
        makeString(listOf(1, 2, 3), ":") shouldBe "1:2:3"
    }

    "makeStringV2(listOf(1, 2, 3), \":\" should return 1:2:3" {
        makeStringV2(listOf(1, 2, 3), ":") shouldBe "1:2:3"
    }

    "makeStringV3(listOf(1, 2, 3), \":\" should return 1:2:3" {
        makeStringV3(listOf(1, 2, 3), ":") shouldBe "1:2:3"
    }

    "listSum(listOf(1, 2, 3, 4)) should return 10" {
        listSum(listOf(1, 2, 3, 4)) shouldBe 10
    }

    "stringV2(listOf('H', 'i')) should return Hi" {
        stringV2(listOf('H', 'i')) shouldBe "Hi"
    }

    "reverse(listOf(1, 2, 3)) should return listOf(3, 2, 1)" {
        reverse(listOf(1, 2, 3)) shouldBe listOf(3, 2, 1)
    }

    "reverseV2(listOf(1, 2, 3)) should return listOf(3, 2, 1)" {
        reverseV2(listOf(1, 2, 3)) shouldBe listOf(3, 2, 1)
    }

    "range(1, 4) should return listOf(1, 2, 3)" {
        range(1, 4) shouldBe listOf(1, 2, 3)
    }

    "rangeV2(1, 4) should return listOf(1, 2, 3)" {
        rangeV2(1, 4) shouldBe listOf(1, 2, 3)
    }

    "rangeV3(1, 4) should return listOf(1, 2, 3)" {
        rangeV3(1, 4) shouldBe listOf(1, 2, 3)
    }

    "rangeV4(1, 4) should return listOf(1, 2, 3)" {
        rangeV4(1, 4) shouldBe listOf(1, 2, 3)
    }

    "fibonacciList(7) should return listOf(1, 1, 2, 3, 5, 8, 13).map { BigDecimal(it)" {
        fibonacciList(7).joinToString() shouldBe "1, 1, 2, 3, 5, 8, 13"
    }

    "iterate(0, 4) { it + 1 } should return listOf(0, 1, 2, 3)" {
        iterate(0, 4) { it + 1 } shouldBe listOf(0, 1, 2, 3)
    }

    "map(listOf('a', 'b', 'c') { it.uppercaseChar() } should return listOf('A', 'B', 'C'))" {
        map(listOf('a', 'b', 'c')) { it.uppercaseChar() } shouldBe listOf('A', 'B', 'C')
    }

    "mapV2(listOf('a', 'b', 'c') { it.uppercaseChar() } should return listOf('A', 'B', 'C'))" {
        mapV2(listOf('a', 'b', 'c')) { it.uppercaseChar() } shouldBe listOf('A', 'B', 'C')
    }

    "fibonacciString(7) should return listOf(1, 1, 2, 3, 5, 8, 13).map { BigDecimal(it)" {
        fibonacciString(7) shouldBe "1, 1, 2, 3, 5, 8, 13"
    }

    "fibonacciStringV2(7) should return listOf(1, 1, 2, 3, 5, 8, 13).map { BigDecimal(it)" {
        fibonacciStringV2(7) shouldBe "1, 1, 2, 3, 5, 8, 13"
    }
})
