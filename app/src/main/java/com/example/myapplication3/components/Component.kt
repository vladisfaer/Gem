package com.gem.framework.components

import com.gem.framework.GameObject
import java.lang.ref.WeakReference

abstract class Component(override var name: String = "Component") : Updatable() {
    val gameObject: GameObject
        get() = parent as GameObject

    override fun update() {}

    override fun updateCheck(): Boolean {
        return true
    }

    override fun onPostInit() {}

    override fun onRemove() {}

    override fun getUpdateOrder(): List<() -> Unit> {
        return listOf {tryUpdate()}
    }

    abstract override fun copy() : Component
}