package pro.artkart.kmagic.list

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

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
})
