package pro.artkart.algs.lafore.stackqueue

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ArrayTest : StringSpec({

    "ArrayStack test" {
        ArrayStack(3).let { stack ->
            stack.push(1)
            stack.push(2)
            stack.push(3)
            stack.peek() shouldBe 3
            shouldThrowWithMessage<IllegalStateException>("Stack is full") {
                stack.push(42)
            }
            stack.pop() shouldBe 3
            stack.pop() shouldBe 2
            stack.pop() shouldBe 1
            shouldThrowWithMessage<IllegalStateException>("Stack is empty") {
                stack.pop()
            }
        }
    }

    "ArrayQueue test" {
        ArrayQueue(3).let { queue ->
            queue.insert(1)
            queue.insert(2)
            queue.insert(3)
            queue.toString() shouldBe "Queue([1, 2, 3])"
            queue.peek() shouldBe 1
            shouldThrowWithMessage<IllegalStateException>("Queue is full") {
                queue.insert(1)
            }
            queue.remove() shouldBe 1
            queue.remove() shouldBe 2
            queue.remove() shouldBe 3
            shouldThrowWithMessage<IllegalStateException>("Queue is empty") {
                queue.remove()
            }
        }
    }

    "infix to postfix fun test" {
        "A*(B+C)-D/(E+F)".toPostfix() shouldBe "ABC+*DEF+/-"
    }

    "evaluate postfix fun test" {
        "345+*612+/-".evaluate().forEach { it shouldBe 25 }
    }
})
