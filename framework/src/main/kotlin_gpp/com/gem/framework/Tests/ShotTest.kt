package com.gem.framework.components

import com.gem.framework.*
import com.gem.framework.events.TouchDownEvent
import com.gem.framework.utils.*

class ShotTest : Component() {

    private val bullet = GameObject("bullet").apply {
        transform.scale = Vector2(0.1f, 0.1f)
        add(RectangleComponent())
        add(TreeAnalyzerComponent())
        add(AutoDesComponent())
        add(Mover())
    }

    override fun onPostInit() {
        rootObject.get<EventBus>()?.subscribe<TouchDownEvent> { event ->
            rootObject.instantiate(bullet).apply {
                transform.position = event.worldPosition()
            }
        }
    }
}
