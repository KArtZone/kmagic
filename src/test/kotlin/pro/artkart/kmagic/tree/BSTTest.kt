package pro.artkart.kmagic.tree

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import pro.artkart.kmagic.exception.Resolution
import pro.artkart.kmagic.list.ImmutableList

class BSTTest : StringSpec({

    val bst = BST(2, 1, 3)

    "created BST should be Empty" {

        BST<Int>().isEmpty() shouldBe true
    }

    "bst shouldBe BST<Int>() + 2 + 1 + 3" {
        bst shouldBe BST<Int>() + 2 + 1 + 3
    }

    "ImmutableList(2, 1, 3).toBST() shouldBe bst" {
        ImmutableList(2, 1, 3).toBST() shouldBe bst
    }

    "BST(ImmutableList(2, 1, 3)) shouldBe bst" {
        BST(ImmutableList(2, 1, 3)) shouldBe bst
    }

    "bst.contains(2) shouldBe true" {
        bst.contains(2) shouldBe true
    }

    "bst.size shouldBe 3" {
        bst.size shouldBe 3
    }

    "bst.height shouldBe 1" {
        bst.height shouldBe 1
    }

    "bst.max() shouldBe Resolution.Success(3)" {
        bst.max() shouldBe Resolution.Success(3)
    }

    "bst.min() shouldBe 0" {
        bst.min() shouldBe Resolution.Success(1)
    }

    "BST(2, 1, 7) + BST(5, 3, 6) shouldBe BST(2, 1, 7, 5, 3, 6)" {
        BST(2, 1, 7) + BST(5, 3, 6) shouldBe BST(2, 1, 7, 5, 3, 6)
    }

    "BST(3, 1, 0, 2, 5, 4, 6).remove(5) shouldBe BST(3, 1, 0, 2, 4, 6)" {
        BST(3, 1, 0, 2, 5, 4, 6).remove(5) shouldBe BST(3, 1, 0, 2, 4, 6)
    }

    "BST(3, 1, 0, 2, 4, 6).remove(5) shouldBe BST(3, 1, 0, 2, 4, 6)" {
        BST(3, 1, 0, 2, 4, 6).remove(5) shouldBe BST(3, 1, 0, 2, 4, 6)
    }

    "foldLeft as sum of bst should return 6" {
        bst.foldLeft(
            0,
            { acc -> { item -> acc + item } }) { a ->
            { b -> a + b }
        } shouldBe 6
    }

    fun strPlus(str1: String): (String) -> String = { str2 -> str1 + str2 }

    "symmetrical fold left on the left should return abcdefg" {
        BST('d', 'b', 'a', 'c', 'f', 'e', 'g')
            .foldLeft("", { i -> { it + i } }, ::strPlus) shouldBe "abcdefg"
    }

    "symmetrical fold right on the left should return abcdefg" {
        BST('d', 'b', 'a', 'c', 'f', 'e', 'g')
            .foldRight("", { i -> { it + i } }, ::strPlus) shouldBe "abcdefg"
    }

    val foldBST = BST(4, 2, 1, 3, 6, 5, 7)

    "foldBST.foldInOrder should return ImmutableList(1, 2, 3, 4, 5, 6, 7)" {
        foldBST.foldInOrder(ImmutableList<Int>()) { left ->
            { item ->
                { right -> left.concat(right.cons(item)) }
            }
        } shouldBe ImmutableList(1, 2, 3, 4, 5, 6, 7)
    }

    "foldBST.foldPreOrder should return ImmutableList(4, 2, 1, 3, 6, 5, 7)" {
        foldBST.foldPreOrder(ImmutableList<Int>()) { item ->
            { left ->
                { right -> left.cons(item).concat(right) }
            }
        } shouldBe ImmutableList(4, 2, 1, 3, 6, 5, 7)
    }

    "foldBST.foldPostOrder should return ImmutableList(4, 2, 1, 3, 6, 5, 7)" {
        foldBST.foldPostOrder(ImmutableList<Int>()) { left ->
            { right ->
                { item -> right.concat(left).cons(item) }
            }
        }.reverseV2() shouldBe ImmutableList(1, 3, 2, 5, 7, 6, 4)
    }

    "BST(left, value, right) should construct BST(5, 3, 2, 8, 9)" {
        BST(
            BST(3, 8),
            5,
            BST(2, 9)
        ) shouldBe BST(5, 3, 2, 8, 9)
    }

    "foldBST.toImmutableList() should return ImmutableList(4, 2, 1, 3, 6, 5, 7)" {
        foldBST.toImmutableList() shouldBe ImmutableList(4, 2, 1, 3, 6, 5, 7)
    }

    "BST(-2, -3, -1).map { it * it } should return BST(4, 1, 9)" {
        BST(-2, -3, -1).map { it * it } shouldBe BST(4, 1, 9)
    }

    "rotateRight() should return BST(2, 1, 4, 3, 6, 5, 7)" {
        foldBST.rotateRight() shouldBe BST(2, 1, 4, 3, 6, 5, 7)
    }

    "rotateLeft() should return BST(6, 4, 2, 1, 3, 5, 7)" {
        foldBST.rotateLeft() shouldBe BST(6, 4, 2, 1, 3, 5, 7)
    }

    "foldBST.maxSum() should return 17" {
        foldBST.maxSum() shouldBe 17
    }

    "foldBST.maxPathSum() should return 22" {
        foldBST.maxPathSum() shouldBe 22
    }

    "toListInOrderRight() should return ImmutableList(1, 2, 3, 4, 5, 6, 7)" {
        foldBST.toListInOrderRight() shouldBe ImmutableList(1, 2, 3, 4, 5, 6, 7)
    }

    "balance() should return BST(4, 2, 1, 3, 6, 5, 7)" {
        BST(1, 2, 3, 4, 5, 6, 7).balance() shouldBe BST(4, 2, 1, 3, 6, 5, 7)
    }

    "balance() should return BST(4, 2, 1, 3, 6, 5)" {
        BST(1, 2, 3, 4, 5, 6).balance() shouldBe BST(3, 2, 1, 5, 4, 6)
    }

    "toPseudoGraphicString() should return pseudo-graphic bst representation" {
        foldBST.toPseudoGraphicString() shouldBe """
            4
            ├── 2
            │   ├── 1
            │   └── 3
            └── 6
                ├── 5
                └── 7
        """.trimIndent() + "\n"
    }

    "BST<Int>().toPseudoGraphicString() should return Empty" {
        BST<Int>().toPseudoGraphicString() shouldBe "Empty"
    }
})
