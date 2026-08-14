package pro.artkart.kmagic.list

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import pro.artkart.kmagic.exception.Either
import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.list.ImmutableList.Companion.max
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
        ImmutableList(1, 2, 3).size shouldBe 3
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

    "max(ImmutableList(1, 2, 42, 3)) should return Either.right(42)" {
        max(ImmutableList(1, 2, 42, 3)) shouldBe Either.right(42)
    }

    "max(ImmutableList<Int>()) should return Either.left(Max called in an empty list)" {
        max(ImmutableList<Int>()) shouldBe Either.left("Max called in an empty list")
    }

    "ImmutableList(1, 2, 3).lastSafe() should return 3" {
        ImmutableList(1, 2, 3).lastSafe() shouldBe Resolution(3)
    }

    "ImmutableList(1, 2, 3).lastSafeV2() should return 3" {
        ImmutableList(1, 2, 3).lastSafeV2() shouldBe Resolution(3)
    }

    "ImmutableList(1, 2, 3).lastSafeV3() should return 3" {
        ImmutableList(1, 2, 3).lastSafeV3() shouldBe Resolution(3)
    }

    "ImmutableList(1, 2, 3).headSafe() should return Resolution(1)" {
        ImmutableList(1, 2, 3).headSafe() shouldBe Resolution(1)
    }

    "ImmutableList(1, 2, 3).headSafeV2() should return Resolution(1)" {
        ImmutableList(1, 2, 3).headSafeV2() shouldBe Resolution(1)
    }

    "ImmutableList(1, 2, 3).headSafeV3() should return Resolution(1)" {
        ImmutableList(1, 2, 3).headSafeV3() shouldBe Resolution(1)
    }

    "ImmutableList(1, 22, 333).unzip { Pair(it, it.length) } to Pair of ImmutableLists" {
        ImmutableList("1", "22", "333").unzip { Pair(it, it.length) } shouldBe Pair(
            ImmutableList("1", "22", "333"),
            ImmutableList(1, 2, 3)
        )
    }

    "ImmutableList(1, 2, 3).getAt(2) shouldBe Resolution(3)" {
        ImmutableList(1, 2, 3).getAt(2) shouldBe Resolution(3)
    }

    "ImmutableList(1, 2, 3).getAt(10) shouldBe Resolution.Empty" {
        ImmutableList(1, 2, 3).getAt(10) shouldBe Resolution.Empty
    }

    "ImmutableList<Int>().getAt(1) shouldBe Resolution.Empty" {
        ImmutableList<Int>().getAt(1) shouldBe Resolution.Empty
    }

    "ImmutableList(1, 2, 3).getAt(-1) shouldBe Resolution.Empty" {
        ImmutableList(1, 2, 3).getAt(-1) shouldBe Resolution.Empty
    }

    "ImmutableList(1, 2, 3).getAtV2(2) shouldBe Resolution(3)" {
        ImmutableList(1, 2, 3).getAtV2(2) shouldBe Resolution(3)
    }

    "ImmutableList(1, 2, 3).getAtV2(10) shouldBe Resolution.Empty" {
        ImmutableList(1, 2, 3).getAtV2(10) shouldBe Resolution.Empty
    }

    "ImmutableList<Int>().getAtV2(1) shouldBe Resolution.Empty" {
        ImmutableList<Int>().getAtV2(1) shouldBe Resolution.Empty
    }

    "ImmutableList(1, 2, 3).getAtV2(-1) shouldBe Resolution.Empty" {
        ImmutableList(1, 2, 3).getAtV2(-1) shouldBe Resolution.Empty
    }

    "ImmutableList(1, 2, 3).getAtV3(2) shouldBe Resolution(3)" {
        ImmutableList(1, 2, 3).getAtV3(2) shouldBe Resolution(3)
    }

    "ImmutableList(1, 2, 3).getAtV3(10) shouldBe Resolution.Empty" {
        ImmutableList(1, 2, 3).getAtV3(10) shouldBe Resolution.Empty
    }

    "ImmutableList<Int>().getAtV3(1) shouldBe Resolution.Empty" {
        ImmutableList<Int>().getAtV3(1) shouldBe Resolution.Empty
    }

    "ImmutableList(1, 2, 3).getAtV3(-1) shouldBe Resolution.Empty" {
        ImmutableList(1, 2, 3).getAtV3(-1) shouldBe Resolution.Empty
    }

    "ImmutableList(1, 2, 3).getAtV4(2) shouldBe Resolution(3)" {
        ImmutableList(1, 2, 3).getAtV4(2) shouldBe Resolution(3)
    }

    "ImmutableList(1, 2, 3).getAtV4(10) shouldBe Resolution.Empty" {
        ImmutableList(1, 2, 3).getAtV4(10) shouldBe Resolution.Empty
    }

    "ImmutableList<Int>().getAtV4(1) shouldBe Resolution.Empty" {
        ImmutableList<Int>().getAtV4(1) shouldBe Resolution.Empty
    }

    "ImmutableList(1, 2, 3).getAtV4(-1) shouldBe Resolution.Empty" {
        ImmutableList(1, 2, 3).getAtV4(-1) shouldBe Resolution.Empty
    }

    "splitAt should split ImmutableList by index to two ImmutableLists" {
        ImmutableList(1, 2, 3, 4).splitAt(2) shouldBe Pair(
            ImmutableList(1, 2),
            ImmutableList(3, 4)
        )
    }

    "splitAtV2 should split ImmutableList by index to two ImmutableLists" {
        ImmutableList(1, 2, 3, 4).splitAtV2(2) shouldBe Pair(
            ImmutableList(1, 2),
            ImmutableList(3, 4)
        )
    }

    "splitAtV3 should split ImmutableList by index to two ImmutableLists" {
        ImmutableList(1, 2, 3, 4).splitAtV3(2) shouldBe Pair(
            ImmutableList(1, 2),
            ImmutableList(3, 4)
        )
    }

    "splitAtV4 should split ImmutableList by index to two ImmutableLists" {
        ImmutableList(1, 2, 3, 4).splitAtV4(2) shouldBe Pair(
            ImmutableList(1, 2),
            ImmutableList(3, 4)
        )
    }

    "ImmutableList(1, 2, 3).hasSublist(ImmutableList(1, 2)) shouldBe true" {
        ImmutableList(1, 2, 3).hasSublist(ImmutableList(2, 3)) shouldBe true
    }

    "ImmutableList(1, 2, 3).hasSublist(ImmutableList(2, 1)) shouldBe false" {
        ImmutableList(1, 2, 3).hasSublist(ImmutableList(2, 1)) shouldBe false
    }

    "groupBy should group by lambda to map" {
        ImmutableList(1, 2, 3, 5, 7, 42).groupBy { it % 2 == 0 } shouldBe mutableMapOf(
            true to ImmutableList(2, 42),
            false to ImmutableList(1, 3, 5, 7)
        )
    }

    "groupByV2 should group by lambda to map" {
        ImmutableList(1, 2, 3, 5, 7, 42).groupByV2 { it % 2 == 0 } shouldBe mutableMapOf(
            true to ImmutableList(2, 42),
            false to ImmutableList(1, 3, 5, 7)
        )
    }

    "groupByV3 should group by lambda to map" {
        ImmutableList(1, 2, 3, 5, 7, 42).groupByV3 { it % 2 == 0 } shouldBe mutableMapOf(
            true to ImmutableList(2, 42),
            false to ImmutableList(1, 3, 5, 7)
        )
    }

    "groupByV4 should group by lambda to map" {
        ImmutableList(1, 2, 3, 5, 7, 42).groupByV4 { it % 2 == 0 } shouldBe mutableMapOf(
            true to ImmutableList(2, 42),
            false to ImmutableList(1, 3, 5, 7)
        )
    }

    "ImmutableList(1, 2, 3).exists { it == 2} shouldBe true" {
        ImmutableList(1, 2, 3).exists { it == 2 } shouldBe true
    }

    "ImmutableList(1, 2, 3).existsV2 { it == 2} shouldBe true" {
        ImmutableList(1, 2, 3).existsV2 { it == 2 } shouldBe true
    }

    "ImmutableList(1, 2, 3).existsV2 { it == 7} shouldBe false" {
        ImmutableList(1, 2, 3).existsV2 { it == 7 } shouldBe false
    }

    "ImmutableList(2, 4, 6, 8).forAll { it % 2 == 0 } shouldBe true" {
        ImmutableList(2, 4, 6, 8).forAll { it % 2 == 0 } shouldBe true
    }

    "ImmutableList(2, 4, 6, 8).forAll { it % 2 == 0 } shouldBe false" {
        ImmutableList(2, 4, 1, 6, 8).forAll { it % 2 == 0 } shouldBe false
    }

    "ImmutableList(1, 2, 3, 4).split(2) should split list of half" {
        ImmutableList(1, 2, 3, 4).split(2) shouldBe ImmutableList(
            ImmutableList(1, 2),
            ImmutableList(3, 4)
        )
    }

    "ImmutableList(1, 2, 3, 4).divide(2) should divide to List of four Lists" {
        ImmutableList(1, 2, 3, 4).divide(2) shouldBe ImmutableList(
            ImmutableList(1),
            ImmutableList(2),
            ImmutableList(3),
            ImmutableList(4)
        )
    }
})
