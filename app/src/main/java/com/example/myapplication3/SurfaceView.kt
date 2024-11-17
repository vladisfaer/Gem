package com.gem.framework

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

lateinit var globContext: Context
class SurfaceView(private val context: Context) : GLSurfaceView(context) {

    init {
        setEGLContextClientVersion(2)
        setRenderer(GLRenderer())
        globContext = context
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Публикуем событие с передачей данных о касании.
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //EventBus.post("touch_event_down", event)
            }
            MotionEvent.ACTION_MOVE -> {
                //EventBus.post("touch_event_move", event)
            }
            MotionEvent.ACTION_UP -> {
                //EventBus.post("touch_event_up", event)
            }
            MotionEvent.ACTION_CANCEL -> {
                //EventBus.post("touch_event_cancel", event)
            }
        }
        return true
    }
}