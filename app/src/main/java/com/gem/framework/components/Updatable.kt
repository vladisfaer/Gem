package com.gem.framework.components

import com.gem.framework.GameObject

abstract class Updatable(open var name: String = "Updatable") {

    var initialized: Boolean = false
    open var parent: GameObject? = null

    fun tryUpdate() {
        if (updateCheck()) {
            update()
        }
    }

    protected open fun update() {}

    protected open fun updateCheck(): Boolean {
        return true
    }

    fun postInit() {
        initialized = true
        onPostInit()
    }

    open fun onPostInit() {

    }

    open fun onRemove() {}

    abstract fun getUpdateOrder() : List<() -> Unit>

    abstract fun copy() : Updatable
}