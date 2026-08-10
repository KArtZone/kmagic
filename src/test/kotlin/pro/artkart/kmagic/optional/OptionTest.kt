package pro.artkart.kmagic.optional

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class OptionTest : StringSpec({

    "Option().getOrElse { 1 } should return 1" {
        Option<Int>().getOrElse { 1 } shouldBe 1
    }

    "Option(42).getOrElse { 1 } should return 42" {
        Option(42).getOrElse { 1 } shouldBe 42
    }

    "Option(1).flatMap { Option<Double>() } should return Option.None" {
        Option(1).flatMap { Option<Double>() } shouldBe Option.None
    }

    "Option(1).flatMapV2 { Option<Double>() } should return Option.None" {
        Option(1).flatMapV2 { Option<Double>() } shouldBe Option.None
    }

    "Option(1).flatMap { Option(it.toDouble()) } should return Option(1.0)" {
        Option(1).flatMap { Option(it.toDouble()) } shouldBe Option(1.0)
    }

    "Option(1).flatMapV2 { Option(it.toDouble()) } should return Option(1.0)" {
        Option(1).flatMapV2 { Option(it.toDouble()) } shouldBe Option(1.0)
    }

    "Option<Int>().orElse { Option(42) } should return Option(42)" {
        Option<Int>().orElse { Option(42) } shouldBe Option(42)
    }

    "Option(7).orElse { Option(42) } should return Option(42)" {
        Option(7).orElse { Option(42) } shouldBe Option(7)
    }

    "Option(2).filter { it % 2 == 0 } should return Option(2)" {
        Option(2).filter { it % 2 == 0 } shouldBe Option(2)
    }
})
