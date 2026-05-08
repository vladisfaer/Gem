package com.gem.framework.utils

import com.gem.framework.*
import java.lang.ref.WeakReference

class Transform2D(val gameObject: GameObject) {
    private var localPosition: Vector2 = Vector2.Zero
    private var localRotation: Float = 0f
    private var localScale: Vector2 = Vector2.One

    @Transient private var parent: WeakReference<Transform2D>? = null
    @Transient private var hasParent: Boolean = false

    // Кэширование globalMatrix:
    // localStamp бампается на каждое изменение локальных полей;
    // effectiveStamp = localStamp + parent.effectiveStamp, рекурсивно вверх.
    // Если стамп не изменился — отдаём кэш без перемножений.
    @Transient private var localStamp: Long = 0L
    @Transient private var cachedStamp: Long = -1L
    @Transient private var cachedMatrix: Matrix3? = null
    
    fun setData(transformData: TransformData2D) {
        localPosition = transformData.localPosition
        localRotation = transformData.localRotation
        localScale = transformData.localScale
    }
    
    fun getData(): TransformData2D {
        return TransformData2D(localPosition, localRotation, localScale)
    }
    
    init {
        updateParentReference()
    }

    var position: Vector2
        get() = localPosition
        set(value) { localPosition = value; bump() }

    var rotation: Float
        get() = localRotation
        set(value) { localRotation = value; bump() }

    var scale: Vector2
        get() = localScale
        set(value) { localScale = value; bump() }

    /**
     * Глобальная позиция = parent.globalMatrix * localPosition.
     * Setter теперь корректно учитывает поворот и масштаб родителя:
     * считает обратное преобразование, а не просто вычитает позицию.
     */
    var globalPosition: Vector2
        get() {
            val p = parent?.get()
            return if (hasParent && p != null) p.globalMatrix().transform(localPosition) else localPosition
        }
        set(value) {
            val p = parent?.get()
            localPosition = if (hasParent && p != null) {
                p.globalMatrix().invert().transform(value)
            } else {
                value
            }
            bump()
        }

    var globalRotation: Float
        get() {
            val p = parent?.get()
            return if (hasParent && p != null) p.globalRotation + localRotation else localRotation
        }
        set(value) {
            val p = parent?.get()
            localRotation = value - if (hasParent && p != null) p.globalRotation else 0f
            bump()
        }

    /**
     * Глобальный масштаб считается как покомпонентное произведение по иерархии.
     * Для случаев «масштаб + поворот родителя» это приближение, как в Unity:
     * шорткат для axis-aligned scale.
     */
    val globalScale: Vector2
        get() {
            val p = parent?.get()
            return if (hasParent && p != null) {
                val ps = p.globalScale
                Vector2(ps.x * localScale.x, ps.y * localScale.y)
            } else {
                localScale
            }
        }

    fun updateParentReference() {
        val parentObject = gameObject.parent
        hasParent = parentObject != null
        parent = if (hasParent) WeakReference(parentObject?.transform) else null
        bump()
    }

    private fun bump() {
        localStamp++
    }

    private val effectiveStamp: Long
        get() = localStamp + (parent?.get()?.effectiveStamp ?: 0L)

    fun globalMatrix(): Matrix3 {
        val stamp = effectiveStamp
        val cached = cachedMatrix
        if (cached != null && cachedStamp == stamp) return cached

        val local = Matrix3()
            .translate(localPosition)
            .rotate(localRotation)
            .scale(localScale)
        val p = parent?.get()
        val global = if (hasParent && p != null) p.globalMatrix() * local else local

        cachedMatrix = global
        cachedStamp = stamp
        return global
    }
}
