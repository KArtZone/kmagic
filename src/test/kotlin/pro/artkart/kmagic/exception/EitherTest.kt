package pro.artkart.kmagic.exception

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EitherTest : StringSpec({

    "Either.right<String, Int>(42).orElse { Either.right(7) } should return Either.right(42)" {
        Either.right<String, Int>(42).orElse { Either.right(7) } shouldBe Either.right(42)
    }

    "Either.left<String, Int>(\"Error\").orElse { Either.right(7) } should return Either.right(7)" {
        Either.left<String, Int>("Error").orElse { Either.right(7) } shouldBe Either.right(7)
    }
})
