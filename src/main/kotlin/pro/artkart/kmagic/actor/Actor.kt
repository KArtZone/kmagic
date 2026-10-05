package pro.artkart.kmagic.actor

import pro.artkart.kmagic.common.Result

interface Actor<T> {

    val context: ActorContext<T>

    fun self(): Result<Actor<T>> = Result(this)

    fun tell(message: T, sender: Result<Actor<T>> = self())

    fun tell(message: T, sender: Actor<T>) = tell(message, Result(sender))

    fun shutdown()

    companion object {

        fun <T> noSender(): Result<Actor<T>> = Result()
    }
}

interface ActorContext<T> {

    fun behavior(): MessageProcessor<T>

    fun become(behavior: MessageProcessor<T>)
}

interface MessageProcessor<T> {

    fun process(message: T, sender: Result<Actor<T>>)
}
