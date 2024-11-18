package com.gem.framework

import java.lang.ref.WeakReference
import com.gem.framework.utils.*
import com.gem.framework.components.*

class GameObject(val name: String = "GameObject") {
    val transform = Transform(this)
    var initialized = false
    public val components = mutableListOf<Component>()
    private val children = mutableListOf<GameObject>()
    private var updateOrder: List<() -> Unit> = emptyList()
    private var changedUpdateOrder = true

    var parent: GameObject? = null
        set(value) {
            field = value
            transform.updateParentReference()
            notifyParentOfChange()
        }

    fun addComponent(component: Component): Component {
        component.gameObject = this
        components.add(component)
        component.postInit()
        notifyParentOfChange()
        return component
    }

    inline fun <reified T : Component> getComponent(): T? {
        return components.firstOrNull { it is T } as? T
    }

    fun removeComponent(component: Component) {
        if (components.contains(component)) {
            component.onRemove()
            components.remove(component)
            notifyParentOfChange()
        }
    }

    fun addChild(child: GameObject) {
        if (child.initialized) { return }
        child.parent = this
        children.add(child)
        notifyParentOfChange()
    }

    fun removeChild(child: GameObject) {
        children.remove(child)
        notifyParentOfChange()
    }

    fun getChildByIndex(index: Int): GameObject {
        return children[index]
    }

    fun childCount(): Int {
        return children.size
    }

    private fun notifyParentOfChange() {
        changedUpdateOrder = true
        parent?.notifyParentOfChange()
    }

    fun rebuildUpdateOrder(): List<() -> Unit> {
        if (!changedUpdateOrder) return updateOrder // Если изменений нет, возвращаем старый порядок
        changedUpdateOrder = false

        val childUpdates = children.flatMap { it.rebuildUpdateOrder() }
        val componentUpdates = components.map {{ it.tryUpdate() }}
        updateOrder = componentUpdates + childUpdates
        return updateOrder
    }

    fun tryUpdateAll() {
        if (changedUpdateOrder) rebuildUpdateOrder()
        updateOrder.forEach { it.invoke() }
    }

    fun postInit() {
        initialized = true
        components.forEach { it.postInit() }
        children.forEach { it.postInit() }
    }

    fun destroy() {
        components.reversed().forEach { it.onRemove() }
        components.clear()
        children.forEach { it.destroy() }
        children.clear()
        parent?.removeChild(this)
    }

    fun instantiate(child: GameObject): GameObject {
        val copy = child.copyForInstantiate()
        this.addChild(copy)
        return copy
    }

    private fun copyForInstantiate(): GameObject {
        val copy = GameObject(name)
        copy.transform.position = transform.position
        copy.transform.rotation = transform.rotation
        copy.transform.scale = transform.scale
        copy.parent = parent
        copy.components.addAll(components.map { it.copy() })
        copy.children.addAll(children.map { it.copyForInstantiate() })
        copy.postInit()
        return copy
    }
}