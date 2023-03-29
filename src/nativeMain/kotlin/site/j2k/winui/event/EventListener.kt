package site.j2k.winui.event

fun interface EventHandler<T: Event> {
    operator fun invoke(event: T)
}

interface IEventListener {
    fun <T : Event> onEvent(eventType: EventType<T>, handler: EventHandler<T>)
    fun <T : Event> dispatch(eventType: EventType<T>, event: T)
}

open class EventListener : IEventListener {
    protected val eventListeners =
        mutableMapOf<EventType<*>, MutableList<EventHandler<*>>>()

    override fun <T : Event> onEvent(eventType: EventType<T>, handler: EventHandler<T>) {
        eventListeners.getOrPut(eventType) { mutableListOf() }.add(handler)
    }
    override fun <T : Event> dispatch(eventType: EventType<T>, event: T) {
        eventListeners[eventType]?.forEach { handler ->
            (handler as EventHandler<T>).invoke(event)
        }
    }
}
