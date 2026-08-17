package pro.artkart.kmagic.lazy

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DeferredTest : StringSpec({

    "Deferred { 42 }() should return 42" {
        Deferred { 42 }() shouldBe 42
    }
})
