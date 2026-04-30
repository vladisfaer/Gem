package com.gem.framework

import android.app.Activity
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.gem.framework.components.EventBus
import com.gem.framework.events.TouchCancelEvent
import com.gem.framework.events.TouchDownEvent
import com.gem.framework.events.TouchMoveEvent
import com.gem.framework.events.TouchUpEvent
import com.gem.framework.utils.Vector2

lateinit var globContext: Activity

class SurfaceView(private val context: Activity) : GLSurfaceView(context) {
    private var eventBus: EventBus? = null

    init {
        setEGLContextClientVersion(2)
        setRenderer(GLRenderer())
        globContext = context
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val bus = eventBus ?: rootObject.get<EventBus>()?.also { eventBus = it } ?: return true

        val actionIndex = event.actionIndex
        val activePointerId = event.getPointerId(actionIndex)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val screen = Vector2(event.getX(actionIndex), event.getY(actionIndex))
                val viewport = Camera.screenToViewport(screen)
                bus.post(TouchDownEvent(activePointerId, screen, viewport))
                bus.post("touch_event_down", screen)
            }
            MotionEvent.ACTION_MOVE -> {
                // ACTION_MOVE не имеет actionIndex — публикуем по событию на каждый активный палец.
                for (i in 0 until event.pointerCount) {
                    val pid = event.getPointerId(i)
                    val screen = Vector2(event.getX(i), event.getY(i))
                    val viewport = Camera.screenToViewport(screen)
                    bus.post(TouchMoveEvent(pid, screen, viewport))
                }
                bus.post("touch_event_move", null)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val screen = Vector2(event.getX(actionIndex), event.getY(actionIndex))
                val viewport = Camera.screenToViewport(screen)
                bus.post(TouchUpEvent(activePointerId, screen, viewport))
                bus.post("touch_event_up", screen)
            }
            MotionEvent.ACTION_CANCEL -> {
                val screen = Vector2(event.getX(actionIndex), event.getY(actionIndex))
                val viewport = Camera.screenToViewport(screen)
                bus.post(TouchCancelEvent(activePointerId, screen, viewport))
                bus.post("touch_event_cancel", screen)
            }
        }
        return true
    }
}
