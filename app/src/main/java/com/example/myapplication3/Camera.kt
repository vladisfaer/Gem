package com.gem.framework

import android.util.DisplayMetrics
import com.gem.framework.utils.*

object Camera {
    private var targetTransform: Transform2D? = null

    fun setTarget(gameObject: GameObject) {
        targetTransform = gameObject.transform
    }

    fun toScreenPosition(worldPosition: Vector2): Vector2 {
        val cameraTransform = targetTransform ?: return worldPosition
        val inverseMatrix = (cameraTransform.globalMatrix()*Matrix3().scale(0.5f,0.5f)).invert()
        return inverseMatrix.transform(worldPosition)
    }

    fun toWorldPosition(screenPosition: Vector2): Vector2 {
        val displayMetrics = DisplayMetrics()
        globContext.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val screenNPos = Vector2(screenPosition.x/width - 0.5f, -screenPosition.y/height + 0.5f)
        val cameraTransform = targetTransform ?: return screenNPos
        return cameraTransform.globalMatrix().transform(screenNPos)
    }

    fun toScreenRotation(worldRotation: Float): Float {
        val cameraRotation = targetTransform?.globalRotation ?: 0f
        return worldRotation - cameraRotation
    }
}