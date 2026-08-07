package pro.artkart.kmagic.list

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ImmutableListTest: StringSpec({

    "ImmutableList(1, 2, 3).toString() should return [1, 2, 3, Nil]" {
        ImmutableList(1, 2, 3).toString() shouldBe "[1, 2, 3, Nil]"
    }
})
