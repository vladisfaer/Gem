package com.gem.framework.components

import com.gem.framework.events.EventData
import kotlin.reflect.KClass

class EventBus : Component() {

    private val nameListeners = mutableMapOf<String, MutableList<(Any?) -> Unit>>()
    private val typedListeners = mutableMapOf<KClass<out EventData>, MutableList<(EventData) -> Unit>>()
    private val delayedNamed = mutableListOf<Pair<String, Any?>>()
    private val delayedTyped = mutableListOf<EventData>()

    /**
     * Заранее регистрирует именованное событие. После этого post() будет
     * буферизовать его, даже если на момент публикации нет слушателей.
     */
    fun register(eventType: String) {
        nameListeners.getOrPut(eventType) { mutableListOf() }
    }

    /**
     * Аналогично, но для типизированных событий (по KClass).
     */
    fun register(eventClass: KClass<out EventData>) {
        typedListeners.getOrPut(eventClass) { mutableListOf() }
    }

    fun subscribe(eventType: String, listener: (Any?) -> Unit) {
        nameListeners.getOrPut(eventType) { mutableListOf() }.add(listener)
    }

    fun <T : EventData> subscribe(eventClass: KClass<T>, listener: (T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        typedListeners.getOrPut(eventClass) { mutableListOf() }
            .add(listener as (EventData) -> Unit)
    }

    inline fun <reified T : EventData> subscribe(noinline listener: (T) -> Unit) {
        subscribe(T::class, listener)
    }

    fun unsubscribe(eventType: String, listener: (Any?) -> Unit) {
        nameListeners[eventType]?.remove(listener)
    }

    fun <T : EventData> unsubscribe(eventClass: KClass<T>, listener: (T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        typedListeners[eventClass]?.remove(listener as (EventData) -> Unit)
    }

    /**
     * Публикация по имени. Если событие не зарегистрировано и нет слушателей —
     * молча игнорируется, чтобы не плодить мусор. Используйте register(),
     * если хотите буферизовать раннюю публикацию.
     */
    fun post(eventType: String, data: Any? = null) {
        if (nameListeners.containsKey(eventType)) {
            delayedNamed.add(eventType to data)
        }
    }

    /**
     * Публикация по объекту EventData. Тип события определяется по его классу.
     */
    fun post(event: EventData) {
        if (typedListeners.containsKey(event::class)) {
            delayedTyped.add(event)
        }
    }

    fun processDelayedEvents() {
        if (delayedNamed.isNotEmpty()) {
            val snapshot = delayedNamed.toList()
            delayedNamed.clear()
            for ((type, data) in snapshot) {
                nameListeners[type]?.forEach { it.invoke(data) }
            }
        }
        if (delayedTyped.isNotEmpty()) {
            val snapshot = delayedTyped.toList()
            delayedTyped.clear()
            for (event in snapshot) {
                typedListeners[event::class]?.forEach { it.invoke(event) }
            }
        }
    }

    fun clearEvent(eventType: String) {
        nameListeners.remove(eventType)
    }

    fun clearEvent(eventClass: KClass<out EventData>) {
        typedListeners.remove(eventClass)
    }

    fun clearAll() {
        nameListeners.clear()
        typedListeners.clear()
        delayedNamed.clear()
        delayedTyped.clear()
    }

    override fun update() {
        processDelayedEvents()
    }

    override fun copy(): EventBus = EventBus()
}
