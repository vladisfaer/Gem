package com.gem.framework

import com.gem.framework.utils.*

/**
 * Камера и преобразования между тремя системами координат:
 *  - World: то, в чём думает игровая логика. Бесконечно, в условных юнитах.
 *  - Viewport: нормированные координаты [-1, 1] по обеим осям, Y вверх.
 *    Не зависят от размера экрана. В этих координатах работает рендерер
 *    (это совпадает с GL NDC).
 *  - Screen: пиксели поверхности (Y вниз, как в Android MotionEvent).
 *
 * Размер вьюпорта в пикселях задаёт GLRenderer.onSurfaceChanged.
 */
object Camera {
    private var targetTransform: Transform2D? = null

    @Volatile
    var viewportSizePx: Vector2 = Vector2(1f, 1f)
        private set

    fun setTarget(gameObject: GameObject) {
        targetTransform = gameObject.transform
    }

    /** Вызывается из GLRenderer при создании/изменении surface. */
    fun onSurfaceResized(widthPx: Int, heightPx: Int) {
        viewportSizePx = Vector2(widthPx.toFloat().coerceAtLeast(1f), heightPx.toFloat().coerceAtLeast(1f))
    }

    // === World <-> Viewport ===

    fun worldToViewport(world: Vector2): Vector2 {
        val cam = targetTransform ?: return world
        return (cam.globalMatrix() * Matrix3().scale(0.5f, 0.5f)).invert().transform(world)
    }

    fun viewportToWorld(viewport: Vector2): Vector2 {
        val cam = targetTransform ?: return viewport
        return (cam.globalMatrix() * Matrix3().scale(0.5f, 0.5f)).transform(viewport)
    }

    // === Viewport <-> Screen ===

    fun viewportToScreen(viewport: Vector2): Vector2 {
        val s = viewportSizePx
        return Vector2((viewport.x + 1f) * s.x * 0.5f, (1f - viewport.y) * s.y * 0.5f)
    }

    fun screenToViewport(screen: Vector2): Vector2 {
        val s = viewportSizePx
        return Vector2(screen.x * 2f / s.x - 1f, 1f - screen.y * 2f / s.y)
    }

    // === World <-> Screen ===

    fun worldToScreen(world: Vector2): Vector2 = viewportToScreen(worldToViewport(world))

    fun screenToWorld(screen: Vector2): Vector2 = viewportToWorld(screenToViewport(screen))

    // === Rotation ===

    fun worldToViewportRotation(rotationDeg: Float): Float {
        val cam = targetTransform?.globalRotation ?: 0f
        return rotationDeg - cam
    }

    fun viewportToWorldRotation(rotationDeg: Float): Float {
        val cam = targetTransform?.globalRotation ?: 0f
        return rotationDeg + cam
    }

    // === Backward-compat (старые имена) ===

    @Deprecated("Используйте worldToViewport()", ReplaceWith("worldToViewport(world)"))
    fun toScreenPosition(world: Vector2): Vector2 = worldToViewport(world)

    @Deprecated("Используйте screenToWorld()", ReplaceWith("screenToWorld(screen)"))
    fun toWorldPosition(screen: Vector2): Vector2 = screenToWorld(screen)

    @Deprecated("Используйте worldToViewportRotation()", ReplaceWith("worldToViewportRotation(rotation)"))
    fun toScreenRotation(rotation: Float): Float = worldToViewportRotation(rotation)
}
