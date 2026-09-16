package pro.artkart.patterns

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import pro.artkart.patterns.behavioral.select

class BehavioralTest : StringSpec({

    "Interpreter should return sql statement" {

        select("name", "age") {
            from("users") {
                where("age > 27")
            }
        }.toString() shouldBe "SELECT name, age FROM users WHERE age > 27"
    }
})
