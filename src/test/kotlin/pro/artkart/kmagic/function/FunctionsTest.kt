package pro.artkart.kmagic.function

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FunctionsTest : StringSpec({
    "sum(2, 3) should return 5" {
        sum(2, 3) shouldBe 5
    }

    "carriedSum(2)(3) should return 5" {
        carriedSum(2)(3) shouldBe 5
        val partlyApplied = carriedSum(2)
        partlyApplied(3) shouldBe 5
    }

    "carriedSumValue(2)(3) should return 5" {
        carriedSumValue(2)(3) shouldBe 5
        val partlyApplied = carriedSum(2)
        partlyApplied(3) shouldBe 5
    }

    "compose(::twice, ::square)(3) should return 36" {
        fun twice(x: Int) = 2 * x
        fun square(x: Int) = x * x

        compose(::twice, ::square)(3) shouldBe 36
    }

    "composeCarriedValue(::twice)(::square)(3) should return 36" {
        fun twice(x: Int) = 2 * x
        fun square(x: Int) = x * x

        composeCarriedValue(::twice)(::square)(3) shouldBe 36
    }

    "composeCarriedValueAliased(::twice)(::square)(3) should return 36" {
        fun twice(x: Int) = 2 * x
        fun square(x: Int) = x * x

        composeCarriedValueAliased(::twice)(::square)(3) shouldBe 36
    }

    "varargCompose(::twice, ::square, ::divideByThree)(3) should return 12" {
        fun twice(x: Int) = 2 * x
        fun square(x: Int) = x * x
        fun divideByThree(x: Int) = x / 3

        varargCompose(::twice, ::square, ::divideByThree)(3) shouldBe 12
    }

    "polyCompose(::repeated, ::uppercased)(3) should return HIHIHI" {
        fun repeated(x: Int) = "hi".repeat(x)
        fun uppercased(s: String) = s.uppercase()

        polyCompose(::repeated, ::uppercased)(3) shouldBe "HIHIHI"
    }

    "polyComposeCarriedValue(::repeated)(::uppercased)(3) should return HIHIHI" {
        val polyComposeCarriedValuePartly = Typed<Int, String, String>().polyComposeCarriedValue { "hi".repeat(it) }
        polyComposeCarriedValuePartly { it.uppercase() }(3) shouldBe "HIHIHI"
    }
})
