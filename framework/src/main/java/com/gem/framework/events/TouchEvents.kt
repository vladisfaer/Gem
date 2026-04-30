package com.gem.framework.events

import com.gem.framework.Camera
import com.gem.framework.utils.Vector2

/**
 * События касания экрана. Не зависят от Android `MotionEvent` — внутри только
 * собственные структуры движка. Преобразование `MotionEvent` → `TouchEvent`
 * выполняет SurfaceView.
 *
 * Координаты доступны в двух системах:
 *  - screenPosition: пиксели поверхности (как в `MotionEvent.getX/getY`)
 *  - viewportPosition: нормализованные [-1, 1], Y вверх (resolution-independent)
 *
 * Мировую позицию можно получить через `worldPosition()` — учитывает текущую камеру.
 */
abstract class TouchEvent(
    val pointerId: Int,
    val screenPosition: Vector2,
    val viewportPosition: Vector2
) : EventData() {
    fun worldPosition(): Vector2 = Camera.viewportToWorld(viewportPosition)
}

class TouchDownEvent(pointerId: Int, screenPosition: Vector2, viewportPosition: Vector2) :
    TouchEvent(pointerId, screenPosition, viewportPosition)

class TouchMoveEvent(pointerId: Int, screenPosition: Vector2, viewportPosition: Vector2) :
    TouchEvent(pointerId, screenPosition, viewportPosition)

class TouchUpEvent(pointerId: Int, screenPosition: Vector2, viewportPosition: Vector2) :
    TouchEvent(pointerId, screenPosition, viewportPosition)

class TouchCancelEvent(pointerId: Int, screenPosition: Vector2, viewportPosition: Vector2) :
    TouchEvent(pointerId, screenPosition, viewportPosition)
