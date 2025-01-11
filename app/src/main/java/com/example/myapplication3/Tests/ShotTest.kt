package com.gem.framework.components

import com.gem.framework.*
import com.gem.framework.utils.*
//TODO: заменить android.view.MotionEvent на аналогичный com.gem.framework.utils.MotionEvent
import android.view.MotionEvent

public class ShotTest : Component() {

    private val bullet = GameObject("bullet").apply{
        transform.scale = Vector3(0.1f,0.1f,0.1f)
        addComponent(SquareTest())
    }

    override fun postInit() {
        rootObject.getComponent<EventBus>()!!.subscribe("touch_event_down") { event ->
            shoot(event as MotionEvent)
        }
    }
    
    fun shoot(event: MotionEvent) {
        val x = event.getAxisValue(MotionEvent.AXIS_X) / 1080f - 0.5f
        val y = -event.getAxisValue(MotionEvent.AXIS_Y) / 2160f + 0.5f
        rootObject.instantiate(bullet).apply{
            transform.position = Vector3(2f*x,2f*y,0.0f)
        }
    }
    
    override fun copy() : ShotTest {
        return ShotTest()
    }
}
