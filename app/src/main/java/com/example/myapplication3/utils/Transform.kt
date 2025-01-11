package com.gem.framework.utils

import com.gem.framework.*
import java.lang.ref.WeakReference

class Transform(val gameObject: GameObject) {
    private var localPosition = Vector3.Zero
    private var localRotation = Vector3.Zero
    private var localScale = Vector3.One

    private var parent: WeakReference<Transform>? = null
    private var hasParent: Boolean = false

    init {
        updateParentReference()
    }

    var position: Vector3
        get() = localPosition
        set(value) { localPosition = value }

    var rotation: Vector3
        get() = localRotation
        set(value) { localRotation = value }

    var scale: Vector3
        get() = localScale
        set(value) { localScale = value }

    val globalPosition: Vector3
        get() = if (hasParent) parent?.get()?.globalMatrix()?.transform(localPosition) ?: localPosition else localPosition

    val globalRotation: Vector3
        get() = if (hasParent) parent?.get()?.globalRotation?.plus(localRotation) ?: localRotation else localRotation

    val globalScale: Vector3
        get() = if (hasParent) parent?.get()?.globalScale?.times(localScale) ?: localScale else localScale

    fun updateParentReference() {
        val parentObject = gameObject.parent
        hasParent = parentObject != null
        parent = if (hasParent) WeakReference(parentObject?.transform) else null
    }

    fun globalMatrix(): Matrix4 {
        val translation = Matrix4().translate(localPosition)
        val rotation = Matrix4().rotateEuler(localRotation)
        val scaling = Matrix4().scale(localScale)
        val localMatrix = translation * rotation * scaling
        return if (hasParent) parent?.get()?.globalMatrix()?.times(localMatrix) ?: localMatrix else localMatrix
    }
}