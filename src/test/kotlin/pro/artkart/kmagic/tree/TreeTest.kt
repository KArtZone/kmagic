package pro.artkart.kmagic.tree

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import pro.artkart.kmagic.list.ImmutableList

class TreeTest : StringSpec({

    "created Tree should be Empty" {

        Tree<Int>().isEmpty() shouldBe true
    }

    "List(2, 1, 3).toTree() should return Tree<Int>() + 2 + 1 + 3" {
        ImmutableList(2, 1, 3)
            .toTree() shouldBe Tree<Int>() + 2 + 1 + 3
    }

    "Tree(2, 1, 3) shouldBe Tree<Int>() + 2 + 1 + 3" {
        Tree(2, 1, 3) shouldBe Tree<Int>() + 2 + 1 + 3
    }

    "Tree(ImmutableList(2, 1, 3)) shouldBe Tree<Int>() + 2 + 1 + 3" {
        Tree(ImmutableList(2, 1, 3)) shouldBe Tree<Int>() + 2 + 1 + 3
    }

    "Tree(2, 1, 3).contains(2) shouldBe true" {
        Tree(2, 1, 3).contains(2) shouldBe true
    }
})
