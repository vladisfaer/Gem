package com.gem.framework

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.gem.framework.rootObject
import com.gem.framework.components.EventBus

lateinit var globContext: Context
class SurfaceView(private val context: Context) : GLSurfaceView(context) {
    var eventBus: EventBus? = null

    init {
        setEGLContextClientVersion(2)
        setRenderer(GLRenderer())
        globContext = context
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (eventBus == null) {
            eventBus = rootObject.getComponent<EventBus>()
        }
        if (eventBus != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    eventBus!!.post("touch_event_down", event)
                }
                MotionEvent.ACTION_MOVE -> {
                    eventBus!!.post("touch_event_move", event)
                }
                MotionEvent.ACTION_UP -> {
                    eventBus!!.post("touch_event_up", event)
                }
                MotionEvent.ACTION_CANCEL -> {
                    eventBus!!.post("touch_event_cancel", event)
                }
            }
        }
        return true
    }
}