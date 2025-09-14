package com.gem.framework.components;

class EventBus(
    private val listeners: MutableMap<String, MutableList<(Any?) -> Unit>> = mutableMapOf<String, MutableList<(Any?) -> Unit>>(),
    private val delayedEvents: MutableList<Pair<String, Any?>> = mutableListOf<Pair<String, Any?>>()
    ):Component(){
    fun subscribe(eventType: String, listener: (Any?) -> Unit) {
        listeners.computeIfAbsent(eventType) { mutableListOf() }.add(listener)
    }

    fun post(eventType: String, data: Any? = null) {
        if (listeners.containsKey(eventType)) {
            delayedEvents.add(Pair(eventType, data))
        }
    }

    fun processDelayedEvents() {
        for ((eventType, data) in delayedEvents) {
            listeners[eventType]?.forEach { it.invoke(data) }
        }
        delayedEvents.clear()
    }

    fun unsubscribe(eventType: String, listener: (Any?) -> Unit) {
        listeners[eventType]?.remove(listener)
        if (listeners[eventType].isNullOrEmpty()) {
            listeners.remove(eventType)
        }
    }

    fun clearEvent(eventType: String) {
        listeners.remove(eventType)
    }

    fun clearAll() {
        listeners.clear()
    }
    
    override fun update() {
        processDelayedEvents()
    }
    
    override fun copy() : EventBus{
        return EventBus(listeners, delayedEvents)
    }
}
