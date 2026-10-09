package pro.artkart.kmagic.actor

import kotlinx.coroutines.sync.Semaphore
import pro.artkart.kmagic.common.Result


const val limit = 10

fun player(
    id: String,
    sound: String,
    referee: Actor<Int>
): AbstractActor<Int> = object : AbstractActor<Int>(id) {

    override fun onReceive(message: Int, sender: Result<Actor<Int>>) {
        log.info { "$sound - $message" }
        if (message >= limit) {
            referee.tell(message, sender)
        } else {
            sender.forEach(
                onSuccess = { it.tell(message + 1, self()) },
                onFailure = { referee.tell(message, sender) }
            )
        }
    }
}

private val semaphore = Semaphore(1)

suspend fun main() {

    val referee = object : AbstractActor<Int>("Referee") {
        override fun onReceive(message: Int, sender: Result<Actor<Int>>) {
            sender.forEach(onSuccess = {
                log.info { "Game ended after $message shots, final was $it" }
            })
            semaphore.release()
        }
    }

    val player1 = player("Player 1", "Ping", referee)
    val player2 = player("Player 2", "Pong", referee)

    semaphore.acquire()
    player1.tell(1, Result(player2))
    semaphore.acquire()
}
