package pro.artkart.patterns.arrow.error

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import arrow.core.raise.context.zipOrAccumulate
import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import pro.artkart.patterns.arrow.error.model.Donut

open class DonutBox(
    protected val capacity: Int = 0,
    protected val donuts: MutableList<Donut> = mutableListOf<Donut>()
) {

    override fun toString(): String = "DonutBox($donuts)"

    fun check(calories: Int, allergens: Set<String>): List<Either<NonEmptyList<DonutIssue>, Donut>> =
        donuts.map { donut ->
            either {
                zipOrAccumulate(
                    { donut.checkCalories(calories) },
                    { donut.checkAllergens(allergens) }
                ) { _, _ -> donut }
            }
        }
}

sealed interface DonutIssue {
    data class AllergensPresent(val allergensList: Set<String>) : DonutIssue {
        override fun toString(): String =
            "Allergens present: $allergensList"
    }

    data class TooManyCalories(val max: Int, val given: Int) : DonutIssue {
        override fun toString(): String =
            "Calories $given above maximum $max"
    }
}

context(raise: Raise<DonutIssue.TooManyCalories>)
fun Donut.checkCalories(maxCalories: Int): Donut = apply {
    ensure(calories < maxCalories) { DonutIssue.TooManyCalories(maxCalories, calories) }
}

context(raise: Raise<DonutIssue.AllergensPresent>)
fun Donut.checkAllergens(allergens: Set<String>): Donut = also { donut ->
    donut.allergens.intersect(allergens).let { presentAllergens ->
        ensure(presentAllergens.isEmpty()) { DonutIssue.AllergensPresent(presentAllergens) }
    }
}


fun main() {

    val log = KotlinLogging.logger { }

    val box = RaiseDonutBox(2)

    either {
        box + Donut(
            "TONGAN VANILLA BEAN CUSTARD", 1000,
            setOf("Wheat", "Milk")
        ) + Donut(
            "SRI LANKAN CINNAMON SUGAR", 800,
            setOf("Wheat")
        )
    }

    box.check(1700, setOf()).forEach {
        it.onLeft { errors -> errors.forEach { error -> log.error { error } } }
            .onRight { donut -> log.info { "$donut is OK" } }
    }
}
