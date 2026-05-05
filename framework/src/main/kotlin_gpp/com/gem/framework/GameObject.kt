package com.gem.framework

import com.gem.framework.utils.*
import com.gem.framework.components.*

open class GameObject(override var name: String = "GameObject") : Updatable() {
    val transform = Transform2D(this)
    val updatables = mutableListOf<Updatable>()
    protected open var updateList: List<() -> Unit> = emptyList()
    protected open var changedUpdateOrder = true

    override var parent: GameObject? = null
        set(value) {
            field = value
            transform.updateParentReference()
            notifyParentOfChange()
        }

    /** Получить первый Updatable указанного типа. */
    inline fun <reified T : Updatable> get(): T? {
        return updatables.firstOrNull { it is T } as? T
    }

    /** Получить Updatable по индексу в списке. */
    fun get(index: Int): Updatable? = updatables.getOrNull(index)

    /**
     * Получить N-й по счёту Updatable указанного типа.
     * Например, get<RectangleComponent>(2) — третий прямоугольник на объекте.
     *
     * @JvmName нужен, потому что после стирания типов сигнатура совпадает
     * с get(index: Int): Updatable? и без переименования возникает
     * platform declaration clash на JVM.
     */
    @JvmName("getTypedAt")
    inline fun <reified T : Updatable> get(index: Int): T? {
        var count = 0
        for (u in updatables) {
            if (u is T) {
                if (count == index) return u
                count++
            }
        }
        return null
    }

    fun getChildren(): List<GameObject> {
        val children = mutableListOf<GameObject>()
        updatables.forEach {
            if (it is GameObject) {
                children.add(it)
            }
        }
        return children
    }

    fun <T : Updatable> add(updatable: T): T {
        if (updatable.initialized) {
            throw ComponentException(
                "Невозможно добавить уже инициализированный объект '${updatable.name}'. " +
                    "Используйте instantiate() для копирования или сначала уберите его из родителя."
            )
        }
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
        if (parent != null) {
            updatables.reversed().forEach {
                it.onRemove()
            }
            updatables.clear()
            parent?.remove(this)
        }
    }

    fun instantiate(updatable: GameObject): GameObject {
        val copy = updatable.copy() as GameObject
        this.add(copy)
        return copy
    }

    override fun copy(): GameObject {
        val copy = GameObject(name)
        copy.transform.position = transform.position
        copy.transform.rotation = transform.rotation
        copy.transform.scale = transform.scale
        updatables.forEach { copy.add(it.copy()) }
        return copy
    }

    private fun notifyParentOfChange() {
        changedUpdateOrder = true
        parent?.notifyParentOfChange()
    }

    override fun getUpdateOrder(): List<() -> Unit> {
        rebuildUpdateOrder()
        return updateList
    }

    open fun rebuildUpdateOrder() {
        if (!changedUpdateOrder) return
        updateList = updatables.flatMap { it.getUpdateOrder() }
        changedUpdateOrder = false
    }

    fun tryUpdateAll() {
        if (changedUpdateOrder) rebuildUpdateOrder()
        updateList.forEach { it.invoke() }
    }

    public override fun update() {
        tryUpdateAll()
    }

    override fun onPostInit() {
        updatables.forEach { it.postInit() }
    }
}