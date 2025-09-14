package com.gem.framework

import com.example.myapplication3.components.RectangleComponent
import com.example.myapplication3.components.TreeAnalyzerComponent
import com.gem.framework.utils.*
import com.gem.framework.components.*

object BuildScript {
    fun build(){
        rootObject.apply{
            add(EventBus())
            add(ShotTest())
            add(GameObject().apply{
                transform.scale = Vector2(0.5f,0.5f)
                transform.position = Vector2(0.2f,0.4f)
                add(RectangleComponent(col = Color(0f,0f,1f,0f)))
            })
            add(GameObject("CameraObject").apply{
                transform.scale = Vector2(5f,10f)
                add(CameraTargetComponent())
            })
        }
    }
}