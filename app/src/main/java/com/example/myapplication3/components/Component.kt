package com.gem.framework.components

import com.gem.framework.GameObject
import java.lang.ref.WeakReference

abstract class Component {
    lateinit var gameObject: GameObject

    fun tryUpdate() {
        if (updateCheck()) {
            update()
        }
    }

    open fun update() {
        // Основной код обновления
    }

    open fun updateCheck(): Boolean {
        return true // По умолчанию обновление разрешено
    }

    open fun postInit() {
        // Вызывается после добавления в дерево
    }

    open fun onRemove() {
        // Очищает ресурсы компонента при удалении
    }
    
    abstract fun copy() : Component
}