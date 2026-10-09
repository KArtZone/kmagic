package pro.artkart.algs.lafore.array

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HighArrayTest : StringSpec({

    val array = HighArray(1)

    "insert" {
        array + 7
        shouldThrow<ArrayFull> { array + 42 }

    }

    "find" {
        array.contains(7) shouldBe true
        array.contains(42) shouldBe false
    }

    "delete" {
        array - 42 shouldBe false
        array - 7 shouldBe true
    }
})
