package pro.artkart.kmagic.exception

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ResolutionTest : StringSpec({

    "Resolution<Int>().getOrElse { 1 } should return 1" {
        Resolution<Int>().getOrElse { 1 } shouldBe 1
    }

    "Resolution<Int>(42).getOrElse { 1 } should return 42" {
        Resolution(42).getOrElse { 1 } shouldBe 42
    }

    "Resolution(42).orElse { Resolution.Empty } should return Resolution(42)" {
        Resolution(42).orElse { Resolution.Empty } shouldBe Resolution(42)
    }

    "Resolution<Int>().orElse { Resolution.Empty } should return Resolution.Empty" {
        Resolution<Int>().orElse { Resolution.Empty } shouldBe Resolution.Empty
    }

    "Resolution(42).filter { it % 2 == 0 } should return Resolution(42)" {
        Resolution(42).filter { it % 2 == 0 } shouldBe Resolution(42)
    }

    "Resolution(42).filter { it % 2 != 0 } should return Resolution.Empty" {
        Resolution(42).filter { it % 2 != 0 } shouldBe Resolution.Empty
    }

    "Resolution(42).filter(error) { it % 2 == 0 } should return Resolution(42)" {
        Resolution(42).filter("error") { it % 2 == 0 } shouldBe Resolution(42)
    }

    "Resolution(42).filter(error) { it % 2 != 0 } should return Resolution.failure(error)" {
        Resolution(42).filter("error") { it % 2 != 0 } shouldBe Resolution.failure("error")
    }

    "Resolution(42).exists { it % 2 == 0 } should return true" {
        Resolution(42).exists { it % 2 == 0 } shouldBe true
    }

    "Resolution(42).exists { it % 2 != 0 } should return false" {
        Resolution(42).exists { it % 2 != 0 } shouldBe false
    }

    "lift(::intToString)(Resolution(2)) should return Resolution(HiHi)" {
        fun intToString(value: Int): String = "Hi".repeat(value)
        lift(::intToString)(Resolution(2)) shouldBe Resolution("HiHi")
    }

    "lift2(repeatString)(Resolution(2))(Resolution(Hi)) should return Resolution(HiHi)" {
        val repeatString: (Int) -> (String) -> String = { n -> { str -> str.repeat(n) } }
        lift2(repeatString)(Resolution(2))(Resolution("Hi")) shouldBe Resolution("HiHi")
    }

    "lift2V2(repeatString)(Resolution(2))(Resolution(Hi)) should return Resolution(HiHi)" {
        val repeatString: (Int) -> (String) -> String = { n -> { str -> str.repeat(n) } }
        lift2V2(repeatString)(Resolution(2))(Resolution("Hi")) shouldBe Resolution("HiHi")
    }

    "lift of repeatStringSize should return Resolution(5)" {
        val repeatStringSize: (Int) -> (String) -> (String) -> Int = { n ->
            { str -> { addition -> (str.repeat(n) + addition).length } }
        }
        lift3(repeatStringSize)(
            Resolution(2)
        )(Resolution("Hi"))(
            Resolution("!")
        ) shouldBe Resolution(5)
    }

    "map2(Resolution(2), Resolution(\"Hi\"), (repeatString)) should return Resolution(\"HiHi\")" {
        val repeatString: (Int) -> (String) -> String = { n -> { str -> str.repeat(n) } }
        map2(
            Resolution(2), Resolution("Hi"),
            (repeatString)
        ) shouldBe Resolution("HiHi")
    }

    "map2V2(Resolution(2), Resolution(\"Hi\"), (repeatString)) should return Resolution(\"HiHi\")" {
        val repeatString: (Int) -> (String) -> String = { n -> { str -> str.repeat(n) } }
        map2V2(
            Resolution(2), Resolution("Hi"),
            (repeatString)
        ) shouldBe Resolution("HiHi")
    }
})
