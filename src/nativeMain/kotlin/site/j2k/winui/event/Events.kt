package site.j2k.winui.event

import platform.windows.CREATESTRUCT

sealed class Event
interface EventType<T : Event>
open class TypedEvent<T : Event>(open var type: EventType<T>) : Event()

data class CreationEvent(
    val createStruct: CREATESTRUCT
) : TypedEvent<CreationEvent>(CreationEvent) {
    companion object : EventType<CreationEvent>
}

class DestroyEvent : TypedEvent<DestroyEvent>(DestroyEvent) {
    companion object : EventType<DestroyEvent>
}
