package com.gem.framework.components

import com.gem.framework.*
import kotlin.math.roundToInt


class TimerTest : Component() {
    var timer: Float = 0f
    @Transient var teext: TextComponent? = null

    override fun onPostInit(){
        teext = gameObject.get<TextComponent>()
    }
    
    override fun update() {
        timer += deltaTime
        if (teext != null) {
            teext!!.text = timer.roundToInt().toString()
        }
    }
}
