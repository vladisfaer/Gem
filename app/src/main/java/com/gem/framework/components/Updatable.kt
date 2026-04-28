package com.gem.framework.components

import com.gem.framework.GameObject
import com.gem.framework.utils.CopyException
import com.gem.framework.utils.ExceptionHandler
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

abstract class Updatable(open var name: String = "Updatable") {

    var initialized: Boolean = false
    open var parent: GameObject? = null

    fun tryUpdate() {
        try {
            if (updateCheck()) {
                update()
            }
        } catch (e: Throwable) {
            ExceptionHandler.handle(e, this)
        }
    }

    protected open fun update() {}

    protected open fun updateCheck(): Boolean {
        return true
    }

    fun postInit() {
        initialized = true
        try {
            onPostInit()
        } catch (e: Throwable) {
            ExceptionHandler.handle(e, this)
        }
    }

    open fun onPostInit() {}

    open fun onRemove() {}

    abstract fun getUpdateOrder(): List<() -> Unit>

    /**
     * Дефолтный copy() через рефлексию: вызывает primary constructor,
     * подставляя значения свойств с теми же именами, что и параметры конструктора.
     *
     * Если такое поведение не подходит (deep copy, ручная инициализация и т.п.) —
     * переопределите copy() в подклассе.
     */
    open fun copy(): Updatable {
        val kClass = this::class
        val ctor = kClass.primaryConstructor
            ?: throw CopyException(
                "${kClass.simpleName}: нет primary constructor для авто-copy(). Переопределите copy() вручную."
            )
        val props = kClass.memberProperties.associateBy { it.name }
        val args = ctor.parameters.associateWith { param ->
            val prop = props[param.name]
                ?: throw CopyException(
                    "${kClass.simpleName}: не найдено свойство '${param.name}' для авто-copy(). " +
                        "Переименуйте параметр конструктора в одноимённое свойство либо переопределите copy()."
                )
            prop.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            (prop as KProperty1<Any, *>).get(this@Updatable)
        }
        return try {
            ctor.callBy(args) as Updatable
        } catch (e: Throwable) {
            throw CopyException("${kClass.simpleName}: ошибка при вызове конструктора в авто-copy()", e)
        }
    }
}
