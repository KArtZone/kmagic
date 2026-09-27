package pro.artkart.arrow.donuts.model

data class Donut(
    val name: String,
    val calories: Int,
    val allergens: List<String>
)