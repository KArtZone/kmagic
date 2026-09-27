package pro.artkart.arrow.donuts

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import pro.artkart.arrow.donuts.model.Donut

class EitherDonutBox(capacity: Int) : DonutBox(capacity) {

    fun add(donut: Donut): Either<NoSpaceInBox, EitherDonutBox> =
        when {
            donuts.size < capacity -> apply { donuts += donut }.right()
            else -> NoSpaceInBox.left()
        }

    fun delete(name: String): Either<NoSuchDonut, Donut> = donuts
        .find { it.name == name }
        ?.also { donuts.remove(it) }?.right()
        ?: NoSuchDonut(name).left()
}

object NoSpaceInBox

data class NoSuchDonut(val name: String)
