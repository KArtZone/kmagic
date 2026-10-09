package pro.artkart.kmagic.actor

import pro.artkart.kmagic.common.Result
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ThreadFactory

abstract class AbstractActor<T>(
    protected val id: String
) : Actor<T> {

    override val context: ActorContext<T> = object : ActorContext<T> {

        var behavior: MessageProcessor<T> = object : MessageProcessor<T> {
            override fun process(message: T, sender: Result<Actor<T>>) {
                onReceive(message, sender)
            }

        }

        override fun behavior(): MessageProcessor<T> = behavior

        @Synchronized
        override fun become(behavior: MessageProcessor<T>) {
            this.behavior = behavior
        }
    }

    private val executor = Executors.newSingleThreadExecutor(DaemonThreadFactory())

    abstract fun onReceive(message: T, sender: Result<Actor<T>>)

    override fun self(): Result<Actor<T>> = Result(this)

    override fun shutdown() = executor.shutdown()

    @Synchronized
    override fun tell(message: T, sender: Result<Actor<T>>) = executor.execute {
        try {
            context.behavior().process(message, sender)
        } catch (e: RejectedExecutionException) {
            log.info { "Canceled" }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun toString(): String = "Actor #$id"
}

class DaemonThreadFactory : ThreadFactory {

    override fun newThread(runnableTask: Runnable): Thread {
        val thread = Executors.defaultThreadFactory().newThread(runnableTask)
        thread.isDaemon = true
        return thread
    }
}
