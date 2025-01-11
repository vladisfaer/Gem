package com.gem.framework.components;

import com.gem.framework.*

public class EventBus(
    // Слушатели для выполнения в конце кадра
    private val listeners: MutableMap<String, MutableList<(Any?) -> Unit>> = mutableMapOf<String, MutableList<(Any?) -> Unit>>(),
    // Очередь отложенных событий для обработки в конце кадра
    private val delayedEvents: MutableList<Pair<String, Any?>> = mutableListOf<Pair<String, Any?>>()
    ):Component(){
    // Метод подписки для выполнения в конце кадра
    fun subscribe(eventType: String, listener: (Any?) -> Unit) {
        listeners.computeIfAbsent(eventType) { mutableListOf() }.add(listener)
    }

    // Универсальный метод для отправки события
    fun post(eventType: String, data: Any? = null) {
        // Добавляем событие в очередь для отложенных слушателей
        if (listeners.containsKey(eventType)) {
            delayedEvents.add(Pair(eventType, data))
        }
    }

    // Метод для обработки всех отложенных событий в конце кадра
    fun processDelayedEvents() {
        for ((eventType, data) in delayedEvents) {
            listeners[eventType]?.forEach { it.invoke(data) }
        }
        // Очищаем очередь отложенных событий после обработки
        delayedEvents.clear()
    }
    
    // Отписка от отложенного события
    fun unsubscribe(eventType: String, listener: (Any?) -> Unit) {
        listeners[eventType]?.remove(listener)
        if (listeners[eventType].isNullOrEmpty()) {
            listeners.remove(eventType)
        }
    }
    
    // Очистка всех подписчиков для конкретного события
    fun clearEvent(eventType: String) {
        listeners.remove(eventType)
    }
    
    // Очистка всех подписчиков
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
