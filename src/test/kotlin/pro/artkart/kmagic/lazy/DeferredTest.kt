package pro.artkart.kmagic.lazy

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import pro.artkart.kmagic.lazy.Deferred.Companion.lift2
import pro.artkart.kmagic.lazy.Deferred.Companion.lift2V2
import pro.artkart.kmagic.lazy.Deferred.Companion.lift2V3
import pro.artkart.kmagic.list.ImmutableList

class DeferredTest : StringSpec({

    "Deferred { 42 }() should return 42" {
        Deferred { 42 }() shouldBe 42
    }

    "Deferred { 42 }.flatMap { Deferred { it / 2 } }() shouldBe 21" {
        Deferred { 42 }.flatMap { Deferred { it / 2 } }() shouldBe 21
    }

    "forEach should assign value 2 to result" {
        var result = 0
        Deferred { 2 }.forEach(true, successEffect = { result = it })
        result shouldBe 2
    }

    "forEach should assign value 3 to result" {
        var result = 0
        Deferred { 2 }.forEach(false, failureEffect = { result = 3 })
        result shouldBe 3
    }

    "forEach should assign value 4 to result" {
        var result = 0
        Deferred { 4 }.forEach(true, successEffect = { r -> result = r }, failureEffect = { r -> result = r })
        result shouldBe 4
    }

    "forEach should assign value 5 to result" {
        var result = 0
        Deferred { 4 }.forEach(false, successEffect = { r -> result = r }, failureEffect = { _ -> result = 5 })
        result shouldBe 5
    }

    "lift2 should convert (String) -> (String) -> String to (Deferred<String>) -> (Deferred<String>) -> Deferred<String>" {
        val f: (String) -> (String) -> String = { s1 -> { s2 -> "$s1, $s2!" } }
        var result = ""
        lift2(f)(Deferred { "Hi" })(Deferred { "Art" }).forEach(true, successEffect = { result = it })
        result shouldBe "Hi, Art!"
    }

    "lift2V2 should convert (String) -> (String) -> String to (Deferred<String>) -> (Deferred<String>) -> Deferred<String>" {
        val f: (String) -> (String) -> String = { s1 -> { s2 -> "$s1, $s2!" } }
        var result = ""
        lift2V2(f)(Deferred { "Hi" })(Deferred { "Art" }).forEach(true, successEffect = { result = it })
        result shouldBe "Hi, Art!"
    }

    "lift2V3 should convert (String) -> (String) -> String to (Deferred<String>) -> (Deferred<String>) -> Deferred<String>" {
        val f: (String) -> (String) -> String = { s1 -> { s2 -> "$s1, $s2!" } }
        var result = ""
        lift2V3(f)(Deferred { "Hi" })(Deferred { "Art" }).forEach(true, successEffect = { result = it })
        result shouldBe "Hi, Art!"
    }

    "constructMessage should return Hi, Art!" {
        var result = ""
        constructMessage(Deferred { "Hi" })(Deferred { "Art" })
            .forEach(true, successEffect = { result = it })
        result shouldBe "Hi, Art!"
    }

    "constructMessage() should return Hi, Art!" {
        var result = ""
        constructMessage(Deferred { "Hi" }, Deferred { "Art" })
            .forEach(true, successEffect = { result = it })
        result shouldBe "Hi, Art!"
    }

    "sequence of ImmutableList of Deferred should return Deferred of ImmutableList of Ints" {
        var list = ImmutableList<Int>()
        sequence(
            ImmutableList(
                Deferred { 1 },
                Deferred { 2 },
                Deferred { 3 }
            )).forEach(true, successEffect = { list = it })
        list shouldBe ImmutableList(1, 2, 3)
    }

    "sequenceResult of ImmutableList of Deferred should return ImmutableList(3, 2, 1)" {
        var list = ImmutableList<Int>()
        sequenceResult(
            ImmutableList(
                Deferred { 1 },
                Deferred { 2 },
                Deferred { 3 }
            )
        ).forEach(true, successEffect = { resolution ->
            resolution.forEach({ list = it })
        })
        list shouldBe ImmutableList(3, 2, 1)
    }

    "sequenceResultV2 of ImmutableList of Deferred should return ImmutableList(1, 2, 3)" {
        var list = ImmutableList<Int>()
        sequenceResultV2(
            ImmutableList(
                Deferred { 1 },
                Deferred { 2 },
                Deferred { 3 }
            )
        ).forEach(true, successEffect = { resolution ->
            resolution.forEach({ list = it })
        })
        list shouldBe ImmutableList(1, 2, 3)
    }

    "sequenceResultV3 of ImmutableList of Deferred should return ImmutableList(1, 2, 3)" {
        var list = ImmutableList<Int>()
        sequenceResultV3(
            ImmutableList(
                Deferred { 1 },
                Deferred { 2 },
                Deferred { 3 }
            )
        ).forEach(true, successEffect = { resolution ->
            resolution.forEach({ list = it })
        })
        list shouldBe ImmutableList(1, 2, 3)
    }
})
