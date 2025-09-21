package com.gem.framework.utils

import com.gem.framework.*
import java.lang.ref.WeakReference

class Transform2D(val gameObject: GameObject) {
    private var localPosition: Vector2 = Vector2.Zero
    private var localRotation: Float = 0f
    private var localScale: Vector2 = Vector2.One

    private var parent: WeakReference<Transform2D>? = null
    private var hasParent: Boolean = false

    init {
        updateParentReference()
    }

    var position: Vector2
        get() = localPosition
        set(value) { localPosition = value }

    var rotation: Float
        get() = localRotation
        set(value) { localRotation = value }

    var scale: Vector2
        get() = localScale
        set(value) { localScale = value }

    var globalPosition: Vector2
        get() = if (hasParent) parent?.get()?.globalMatrix()?.transform(localPosition) ?: localPosition else localPosition
        set(value) { localPosition = value - (gameObject.parent?.transform?.globalPosition ?: Vector2(0f, 0f)) }

    var globalRotation: Float
        get() = if (hasParent) parent?.get()?.globalRotation?.plus(localRotation) ?: localRotation else localRotation
        set(value) { localRotation = value - (gameObject.parent?.transform?.globalRotation ?: 0f) }

    val globalScale: Vector2
        get() {
            val gm = globalMatrix()
            return Vector2(gm.values[0],gm.values[4])
        }

    fun updateParentReference() {
        val parentObject = gameObject.parent
        hasParent = parentObject != null
        parent = if (hasParent) WeakReference(parentObject?.transform) else null
    }

    fun globalMatrix(): Matrix3 {
        val translation = Matrix3().translate(localPosition)
        val rotation = Matrix3().rotate(localRotation)
        val scaling = Matrix3().scale(localScale)
        val localMatrix = translation * rotation * scaling
        return if (hasParent) parent?.get()?.globalMatrix()?.times(localMatrix) ?: localMatrix else localMatrix
    }
}