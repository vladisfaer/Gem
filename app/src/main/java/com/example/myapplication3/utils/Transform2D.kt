package com.gem.framework.utils

import com.gem.framework.*
import java.lang.ref.WeakReference

class Transform2D(val gameObject: GameObject) {
    private var localPosition = Vector2.Zero
    private var localRotation = 0f
    private var localScale = Vector2.One

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

    val globalPosition: Vector2
        get() = if (hasParent) parent?.get()?.globalMatrix()?.transform(localPosition) ?: localPosition else localPosition

    val globalRotation: Float
        get() = if (hasParent) parent?.get()?.globalRotation?.plus(localRotation) ?: localRotation else localRotation

    val globalScale: Vector2
        get() = if (hasParent) parent?.get()?.globalScale?.times(localScale) ?: localScale else localScale

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