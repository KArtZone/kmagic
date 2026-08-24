package pro.artkart.kmagic.tree

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TreeTest : StringSpec({

    "created Tree should be Empty" {

        Tree<Int>().isEmpty() shouldBe true
    }
})
