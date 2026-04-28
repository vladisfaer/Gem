package com.gem.framework.events

abstract class EventData {
    open var name: String = this::class.simpleName ?: "Event"
}
