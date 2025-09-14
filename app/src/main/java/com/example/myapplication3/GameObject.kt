package com.gem.framework

import com.gem.framework.utils.*
import com.gem.framework.components.*

class GameObject(override var name: String = "GameObject") : Updatable() {
    val transform = Transform2D(this)
    val updatables = mutableListOf<Updatable>()
    private var updateOrder: List<() -> Unit> = emptyList()
    private var changedUpdateOrder = true

    override var parent: GameObject? = null
        set(value) {
            field = value
            transform.updateParentReference()
            notifyParentOfChange()
        }

    inline fun <reified T : Updatable> get(): T? {
        return updatables.firstOrNull { it is T } as? T
    }

    fun add(updatable: Updatable): Updatable {
        if (updatable.initialized) { return updatable }
        updatable.parent = this
        updatables.add(updatable)
        if (initialized) {
            updatable.postInit()
            notifyParentOfChange()
        }

        return updatable
    }

    fun remove(updatable: Updatable) {
        if (contains(updatable)) {
            updatable.onRemove()
            updatables.remove(updatable)
            notifyParentOfChange()
        }
    }

    fun contains(updatable: Updatable): Boolean {
        return updatables.contains(updatable)
    }

    fun destroy() {
        updatables.reversed().forEach { it.onRemove() }
        updatables.clear()
        parent?.remove(this)
    }

    fun instantiate(updatable: GameObject): GameObject {
        val copy = updatable.copy()
        this.add(copy)
        return copy
    }
    
    override fun copy(): GameObject {
        val copy = GameObject(name)
        copy.transform.position = transform.position
        copy.transform.rotation = transform.rotation
        copy.transform.scale = transform.scale
        copy.parent = parent
        updatables.forEach{ copy.add(it.copy()) }
        return copy
    }

    private fun notifyParentOfChange() {
        changedUpdateOrder = true
        parent?.notifyParentOfChange()
    }

    override fun getUpdateOrder(): List<() -> Unit> {
        rebuildUpdateOrder()
        return updateOrder
    }

    fun rebuildUpdateOrder() {
        if (!changedUpdateOrder) return

        updateOrder = updatables.flatMap{it.getUpdateOrder()}

        changedUpdateOrder = false
    }

    fun tryUpdateAll() {
        if (changedUpdateOrder) rebuildUpdateOrder()
        updateOrder.forEach { it.invoke() }
    }

    override public fun update() {
        tryUpdateAll()
    }

    override fun onPostInit() {
        updatables.forEach { it.postInit() }
    }
}