package com.gem.framework.components

import com.gem.framework.*
import com.gem.framework.utils.*
//TODO: заменить android.view.MotionEvent на аналогичный com.gem.framework.utils.MotionEvent
import android.view.MotionEvent
import com.example.myapplication3.components.AutoDesComponent
import com.example.myapplication3.components.RectangleComponent
import com.example.myapplication3.components.TreeAnalyzerComponent
import kotlin.random.Random

public class ShotTest : Component() {

    private val bullet = GameObject("bullet").apply{
        transform.scale = Vector2(0.1f,0.1f)
        add(RectangleComponent())
        add(TreeAnalyzerComponent())
        add(AutoDesComponent())
        add(Mover())
    }

    override fun onPostInit() {
        rootObject.get<EventBus>()!!.subscribe("touch_event_down") { event ->
            shoot(event as MotionEvent)
        }
    }
    
    fun shoot(event: MotionEvent) {
        val x = event.getAxisValue(MotionEvent.AXIS_X) / 1080f - 0.5f
        val y = -event.getAxisValue(MotionEvent.AXIS_Y) / 2160f + 0.5f
        rootObject.instantiate(bullet).apply{
            transform.position = Vector2(2f*x,2f*y)
        }
    }
    
    override fun copy() : ShotTest {
        return ShotTest()
    }
}
