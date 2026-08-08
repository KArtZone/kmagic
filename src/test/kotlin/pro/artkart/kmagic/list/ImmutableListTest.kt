package pro.artkart.kmagic.list

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class ImmutableListTest : StringSpec({

    "ImmutableList(1, 2, 3).toString() should return [1, 2, 3, Nil]" {
        ImmutableList(1, 2, 3)
            .toString() shouldBe "[1, 2, 3, Nil]"
    }

    "ImmutableList(1, 2, 3).setHead(42).toString() should return [42, 2, 3, Nil]" {
        ImmutableList(1, 2, 3)
            .setHead(42)
            .toString() shouldBe "[42, 2, 3, Nil]"
    }

    "ImmutableList(1, 2, 3, 4, 5).drop(2).toString should return [3, 4, 5, Nil]" {
        ImmutableList(1, 2, 3, 4, 5)
            .drop(2)
            .toString() shouldBe "[3, 4, 5, Nil]"
    }

    "ImmutableList(1, 2, 3, 4, 5).drop(10).toString should return [Nil]" {
        ImmutableList(1, 2, 3, 4, 5)
            .drop(10)
            .toString() shouldBe "[Nil]"
    }

    "ImmutableList(1, 2, 3, 4, 5).drop(0).toString should return [1, 2, 3, 4, 5, Nil]" {
        ImmutableList(1, 2, 3, 4, 5)
            .drop(0)
            .toString() shouldBe "[1, 2, 3, 4, 5, Nil]"
    }

    "ImmutableList(1, 2, 3, 4, 5).drop(-1).toString should return [1, 2, 3, 4, 5, Nil]" {
        ImmutableList(1, 2, 3, 4, 5)
            .drop(-1)
            .toString() shouldBe "[1, 2, 3, 4, 5, Nil]"
    }

    "ImmutableList(4, 3, 2, 3).drop(2).cons(1).toString() should return [1, 2, 3, Nil]" {
        ImmutableList(4, 3, 2, 3)
            .drop(2)
            .cons(1)
            .toString() shouldBe "[1, 2, 3, Nil]"
    }

    "ImmutableList(1, 2, 3, 4).dropWhile { it < 3}.toString() should return [3, 4, Nil]" {
        ImmutableList(1, 2, 3, 4)
            .dropWhile { it < 3 }
            .toString() shouldBe "[3, 4, Nil]"
    }

    "ImmutableList(1, 2, 3, 4).dropWhile { it == 42}.toString() should return [3, 4, Nil]" {
        ImmutableList(1, 2, 3, 4)
            .dropWhile { it == 42 }
            .toString() shouldBe "[1, 2, 3, 4, Nil]"
    }

    "ImmutableList(1, 2, 3, 4).dropWhile { true }.toString() should return [Nil]" {
        ImmutableList(1, 2, 3, 4)
            .dropWhile { true }
            .toString() shouldBe "[Nil]"
    }

    "ImmutableList(1, 2, 3).concat(ImmutableList(5, 4)).toString() should return [1, 2, 3, 5, 4, Nil]" {
        ImmutableList(1, 2, 3)
            .concat(ImmutableList(5, 4))
            .toString() shouldBe "[1, 2, 3, 5, 4, Nil]"
    }

    "ImmutableList(1, 2, 3, 4).init().toString() should return [1, 2, 3, Nil]" {
        ImmutableList(1, 2, 3, 4).init().toString() shouldBe "[1, 2, 3, Nil]"
    }

    "ImmutableList(1, 2, 3).reverse().toString() should return [3, 2, 1, Nil]" {
        ImmutableList(1, 2, 3).reverse().toString() shouldBe "[3, 2, 1, Nil]"
    }

    "ImmutableList(1, 2, 3).sum() should return 6" {
        ImmutableList(1, 2, 3).sum() shouldBe 6
    }

    "concatFoldRight(ImmutableList(1, 2, 3), ImmutableList(5, 4, 3)).toString() should return [1, 2, 3, 5, 4, 3, Nil]" {
        concatFoldRight(ImmutableList(1, 2, 3), ImmutableList(5, 4, 3))
            .toString() shouldBe "[1, 2, 3, 5, 4, 3, Nil]"
    }

    "concatFoldLeft(ImmutableList(1, 2, 3), ImmutableList(5, 4, 3)).toString() should return [1, 2, 3, 5, 4, 3, Nil]" {
        concatFoldLeft(ImmutableList(1, 2, 3), ImmutableList(5, 4, 3))
            .toString() shouldBe "[1, 2, 3, 5, 4, 3, Nil]"
    }

    "ImmutableList(1, 2, 3).size() should return 3" {
        ImmutableList(1, 2, 3).size() shouldBe 3
    }

    "ImmutableList(1, 2, 3).reverseV2().toString() should return [3, 2, 1, Nil]" {
        ImmutableList(1, 2, 3).reverseV2().toString() shouldBe "[3, 2, 1, Nil]"
    }

    "ImmutableList(1, 2, 3, 4, 5).factorial() should return BigDecimal(120)" {
        ImmutableList(1, 2, 3, 4, 5).factorial() shouldBe BigDecimal(120)
    }

    "ImmutableList(1, 2, 3, 4, 5).factorialV2() should return BigDecimal(120)" {
        ImmutableList(1, 2, 3, 4, 5).factorialV2() shouldBe BigDecimal(120)
    }

    "convert ImmutableList of ImmutableLists to flatten ImmutableList" {
        flatten(
            ImmutableList(
                ImmutableList(1, 2),
                ImmutableList(3, 4, 5),
                ImmutableList(7)
            )
        ).toString() shouldBe "[1, 2, 3, 4, 5, 7, Nil]"
    }

    "triple(ImmutableList(1, 2, 3)).toString() should return [3, 6, 9, Nil]" {
        triple(ImmutableList(1, 2, 3)).toString() shouldBe "[3, 6, 9, Nil]"
    }

    "doubleToString(ImmutableList(2.1, 3.3, 4.2).toString() should return [2.1, 3.3, 4.2, Nil]" {
        doubleToString(ImmutableList(2.1, 3.3, 4.2)).toString() shouldBe "[2.1, 3.3, 4.2, Nil]"
    }

    "ImmutableList(1, 2, 3, 4, 5, 6).filter { it % 2 == 0 }.toString() should return [2, 4, 6, Nil]" {
        ImmutableList(1, 2, 3, 4, 5, 6)
            .filter { it % 2 == 0 }
            .toString() shouldBe "[2, 4, 6, Nil]"
    }

    "ImmutableList(1, 2, 3, 4, 5, 6).filterV2 { it % 2 != 0 }.toString() should return [1, 3, 5, Nil]" {
        ImmutableList(1, 2, 3, 4, 5, 6)
            .filterV2 { it % 2 != 0 }
            .toString() shouldBe "[1, 3, 5, Nil]"
    }
})
