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

    "tree.height shouldBe 1" {
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

    "foldLeft as sum of tree should return 6" {
        tree.foldLeft(
            0,
            { acc -> { item -> acc + item } }) { a ->
            { b -> a + b }
        } shouldBe 6
    }

    fun strPlus(str1: String): (String) -> String = { str2 -> str1 + str2 }

    "symmetrical fold left on the left should return abcdefg" {
        Tree('d', 'b', 'a', 'c', 'f', 'e', 'g')
            .foldLeft("", { i -> { it + i } }, ::strPlus) shouldBe "abcdefg"
    }

    "symmetrical fold right on the left should return abcdefg" {
        Tree('d', 'b', 'a', 'c', 'f', 'e', 'g')
            .foldRight("", { i -> { it + i } }, ::strPlus) shouldBe "abcdefg"
    }

    val foldTree = Tree(4, 2, 1, 3, 6, 5, 7)

    "foldTree.foldInOrder should return ImmutableList(1, 2, 3, 4, 5, 6, 7)" {
        foldTree.foldInOrder(ImmutableList<Int>()) { left ->
            { item ->
                { right -> left.concat(right.cons(item)) }
            }
        } shouldBe ImmutableList(1, 2, 3, 4, 5, 6, 7)
    }

    "foldTree.foldPreOrder should return ImmutableList(4, 2, 1, 3, 6, 5, 7)" {
        foldTree.foldPreOrder(ImmutableList<Int>()) { item ->
            { left ->
                { right -> left.cons(item).concat(right) }
            }
        } shouldBe ImmutableList(4, 2, 1, 3, 6, 5, 7)
    }

    "foldTree.foldPostOrder should return ImmutableList(4, 2, 1, 3, 6, 5, 7)" {
        foldTree.foldPostOrder(ImmutableList<Int>()) { left ->
            { right ->
                { item -> right.concat(left).cons(item) }
            }
        }.reverseV2() shouldBe ImmutableList(1, 3, 2, 5, 7, 6, 4)
    }

    "Tree(left, value, right) should construct Tree(5, 3, 2, 8, 9)" {
        Tree(
            Tree(3, 8),
            5,
            Tree(2, 9)
        ) shouldBe Tree(5, 3, 2, 8, 9)
    }

    "foldTree.toImmutableList() should return ImmutableList(4, 2, 1, 3, 6, 5, 7)" {
        foldTree.toImmutableList() shouldBe ImmutableList(4, 2, 1, 3, 6, 5, 7)
    }

    "Tree(-2, -3, -1).map { it * it } should return Tree(4, 1, 9)" {
        Tree(-2, -3, -1).map { it * it } shouldBe Tree(4, 1, 9)
    }

    "rotateRight() should return Tree(2, 1, 4, 3, 6, 5, 7)" {
        foldTree.rotateRight() shouldBe Tree(2, 1, 4, 3, 6, 5, 7)
    }

    "rotateLeft() should return Tree(6, 4, 2, 1, 3, 5, 7)" {
        foldTree.rotateLeft() shouldBe Tree(6, 4, 2, 1, 3, 5, 7)
    }

    "foldTree.maxSum() should return 17" {
        foldTree.maxSum() shouldBe 17
    }

    "foldTree.maxPathSum() should return 22" {
        foldTree.maxPathSum() shouldBe 22
    }

    "toListInOrderRight() should return ImmutableList(1, 2, 3, 4, 5, 6, 7)" {
        foldTree.toListInOrderRight() shouldBe ImmutableList(1, 2, 3, 4, 5, 6, 7)
    }
})
