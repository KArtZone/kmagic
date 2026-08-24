package pro.artkart.kmagic.lazy

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import pro.artkart.kmagic.list.ImmutableList
import pro.artkart.kmagic.utils.stream
import pro.artkart.kmagic.utils.toImmutableList

class StreamTest : StringSpec({

    "dropAtMost(3).takeAtMost(4).toList() should return ImmutableList(3, 4, 5, 6)" {
        Stream.iterate<Int>(seed = Deferred { 0 }) { it + 1 }
            .dropAtMost(3)
            .takeAtMost(4)
            .toList() shouldBe ImmutableList(3, 4, 5, 6)
    }

    "Stream(3, 4, 5, 6).exists { it == 3 } should return true" {
        Stream.from(0)
            .dropAtMost(3)
            .takeAtMost(4)
            .exists { it == 3 } shouldBe true
    }

    "takeWhileViaFoldRight { it < 3 }.toList() shouldBe ImmutableList(0, 1, 2)" {
        Stream.iterate(0) { it + 1 }
            .takeWhileViaFoldRight { it < 3 }
            .toList() shouldBe ImmutableList(0, 1, 2)
    }

    "headSafe() should return Resolution of head of Stream" {
        var result = 0
        Stream.fromV2(42)
            .takeAtMost(1)
            .headSafe()
            .forEach(onSuccess = { result = it })
        result shouldBe 42
    }

    "flatMap().toList() should return ImmutableList('h', 'i', 't', 'h', 'e')" {
        ImmutableList("hi", "the").stream()
            .flatMap { it.toImmutableList().stream() }
            .toList() shouldBe ImmutableList('h', 'i', 't', 'h', 'e')
    }

    "ImmutableList(1, 2, 3).stream().find { it % 2 == 0 } should return 2" {
        var result = 0
        ImmutableList(1, 2, 3).stream()
            .find { it % 2 == 0 }
            .forEach(onSuccess = { result = it })
        result shouldBe 2
    }

    "fibs().takeAtMost(6).toList() shouldBe ImmutableList(1, 1, 2, 3, 5, 8)" {
        fibs().takeAtMost(6).toList() shouldBe ImmutableList(1, 1, 2, 3, 5, 8)
    }

    "fibsV2().takeAtMost(6).toList() shouldBe ImmutableList(1, 1, 2, 3, 5, 8)" {
        fibsV2().takeAtMost(6).toList() shouldBe ImmutableList(1, 1, 2, 3, 5, 8)
    }
})
