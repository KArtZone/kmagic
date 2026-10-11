package pro.artkart.kmagic.actor

import pro.artkart.kmagic.common.List
import pro.artkart.kmagic.common.Result

class Receiver(
    id: String,
    client: AbstractActor<List<Int>>
) : AbstractActor<Int>(id) {

    private val receiverFunction: (Receiver) -> (Behavior) -> (Int) -> Unit = { receiver ->
        { behavior ->
            { message ->
                if (message == -1) {
                    client.tell(behavior.resultList.reverse())
                    shutdown()
                } else {
                    receiver.context.become(Behavior(behavior.resultList.cons(message)))
                }
            }
        }
    }

    override fun onReceive(
        message: Int,
        sender: Result<Actor<Int>>
    ) {
        context.become(Behavior(List(message)))
    }

    internal inner class Behavior internal constructor(
        internal val resultList: List<Int>
    ) : MessageProcessor<Int> {

        override fun process(message: Int, sender: Result<Actor<Int>>) {
            receiverFunction(this@Receiver)(this@Behavior)(message)
        }
    }
}
