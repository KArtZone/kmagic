package pro.artkart.kmagic.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import pro.artkart.kmagic.list.ImmutableList
import pro.artkart.kmagic.optional.Option

class ListOptionUtilsTest : StringSpec({

    "Option(1).filter { it % 2 == 0 } should return None" {
        Option(1).filter { it % 2 == 0 } shouldBe Option.None
    }

    "variance(ImmutableList(1.0, 2.0, 9.0)) should return Option(12.666666666666666)" {
        variance(ImmutableList(1.0, 2.0, 9.0)) shouldBe Option(12.666666666666666)
    }

    "lift<Int, String> { \"Hi\".repeat(it) }(Option(3)) should return Option(HiHiHi)" {
        lift<Int, String> { "Hi".repeat(it) }(Option(3)) shouldBe Option("HiHiHi")
    }

    "liftWithException" {
        liftWithException<Int, Int> {
            @Suppress("DIVISION_BY_ZERO")
            it / 0
        }(Option(42)) shouldBe Option()
    }

    "sequence of ImmutableList of Options should return Option of ImmutableList" {
        sequence(
            ImmutableList(
                Option(1),
                Option(2),
                Option(3)
            )
        ) shouldBe Option(ImmutableList(1, 2, 3))
    }

    "sequence1 of ImmutableList of Options should return Option of ImmutableList" {
        sequence1(
            ImmutableList(
                Option(1),
                Option(2),
                Option(3)
            )
        ) shouldBe Option(ImmutableList(1, 2, 3))
    }

    "sequence of Options of Strings has return Option of List of Ints" {
        val parseWithRadix: (Int) -> (String) -> Option<Int> = { radix ->
            { string -> Option(Integer.parseInt(string, radix)) }
        }
        val parseHex = parseWithRadix(16)
        val list = ImmutableList("4", "5", "6", "7", "8", "9", "A", "B")
        sequence(list.map(parseHex)) shouldBe Option(
            ImmutableList(
                4, 5, 6, 7, 8, 9, 10, 11
            )
        )
    }

    "sequenceV2 of ImmutableList of Options should return Option of ImmutableList" {
        sequenceV2(
            ImmutableList(
                Option(1),
                Option(2),
                Option(3)
            )
        ) shouldBe Option(ImmutableList(1, 2, 3))
    }
})
