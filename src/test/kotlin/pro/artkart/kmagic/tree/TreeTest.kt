package pro.artkart.kmagic.tree

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.list.ImmutableList

class TreeTest : StringSpec({

    val tree = Tree(2, 1, 3)

    "created Tree should be Empty" {

        Tree<Int>().isEmpty() shouldBe true
    }

    "tree shouldBe Tree<Int>() + 2 + 1 + 3" {
        tree shouldBe Tree<Int>() + 2 + 1 + 3
    }

    "ImmutableList(2, 1, 3).toTree() shouldBe tree" {
        ImmutableList(2, 1, 3).toTree() shouldBe tree
    }

    "Tree(ImmutableList(2, 1, 3)) shouldBe tree" {
        Tree(ImmutableList(2, 1, 3)) shouldBe tree
    }

    "tree.contains(2) shouldBe true" {
        tree.contains(2) shouldBe true
    }

    "tree.size shouldBe 3" {
        tree.size shouldBe 3
    }

    "tree.height shouldBe 2" {
        tree.height shouldBe 1
    }

    "tree.max() shouldBe Resolution.Success(3)" {
        tree.max() shouldBe Resolution.Success(3)
    }

    "tree.min() shouldBe 0" {
        tree.min() shouldBe Resolution.Success(1)
    }

    "Tree(2, 1, 7) + Tree(5, 3, 6) shouldBe Tree(2, 1, 7, 5, 3, 6)" {
        Tree(2, 1, 7) + Tree(5, 3, 6) shouldBe Tree(2, 1, 7, 5, 3, 6)
    }

    "Tree(3, 1, 0, 2, 5, 4, 6).remove(5) shouldBe Tree(3, 1, 0, 2, 4, 6)" {
        Tree(3, 1, 0, 2, 5, 4, 6).remove(5) shouldBe Tree(3, 1, 0, 2, 4, 6)
    }

    "Tree(3, 1, 0, 2, 4, 6).remove(5) shouldBe Tree(3, 1, 0, 2, 4, 6)" {
        Tree(3, 1, 0, 2, 4, 6).remove(5) shouldBe Tree(3, 1, 0, 2, 4, 6)
    }
})
