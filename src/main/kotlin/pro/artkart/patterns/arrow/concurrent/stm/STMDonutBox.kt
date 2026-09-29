package pro.artkart.patterns.arrow.concurrent.stm

import arrow.fx.stm.STM
import arrow.fx.stm.TVar
import arrow.fx.stm.atomically
import io.github.oshai.kotlinlogging.KotlinLogging
import pro.artkart.patterns.arrow.error.DonutBox
import pro.artkart.patterns.arrow.error.model.Donut

class STMDonutBox private constructor(capacity: Int, donuts: MutableList<Donut>) : DonutBox(capacity, donuts) {

    operator fun plus(donut: Donut): STMDonutBox = apply { donuts += donut }

    operator fun minus(name: String): Donut? = donuts
        .find { it.name == name }
        ?.also { donuts.remove(it) }

    override fun toString(): String = "Box($donuts)"

    companion object {

        suspend operator fun invoke(vararg donuts: Donut): TVar<STMDonutBox> =
            TVar.new(STMDonutBox(donuts.size, donuts.toMutableList()))
    }
}

fun STM.add(tBox: TVar<STMDonutBox>, donut: Donut) = tBox.modify { tBox.read() + donut }

fun STM.remove(tBox: TVar<STMDonutBox>, name: String): Donut {
    val box = tBox.read()
    val donut = box - name
    requireNotNull(donut)
    tBox.modify { box }
    return donut
}

fun STM.transfer(source: TVar<STMDonutBox>, target: TVar<STMDonutBox>, name: String) =
    add(target, remove(source, name))

fun STM.str(tBox: TVar<STMDonutBox>) = tBox.read().toString()

suspend fun main() {

    val log = KotlinLogging.logger { }

    val myBox = STMDonutBox(
        Donut("Rum&pecan caramel donut", 1000, emptySet())
    )
    val yourBox = STMDonutBox()

    atomically {
        log.info { "my: ${str(myBox)}" }
        log.info { "your: ${str(yourBox)}" }

        transfer(myBox, yourBox, "Rum&pecan caramel donut")

        log.info { "my: ${str(myBox)}" }
        log.info { "your: ${str(yourBox)}" }
    }
}
