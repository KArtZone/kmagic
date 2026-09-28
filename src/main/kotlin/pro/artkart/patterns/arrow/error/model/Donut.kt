package pro.artkart.patterns.arrow.error.model

data class Donut(
    val name: String,
    val calories: Int,
    val allergens: Set<String>
)