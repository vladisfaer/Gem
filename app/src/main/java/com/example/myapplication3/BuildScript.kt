package com.gem.framework

import com.gem.framework.utils.*
import com.gem.framework.components.*

object BuildScript {
    fun build(){
        rootObject.apply{
        addComponent(ShotTest())
        addComponent(EventBus())
        addChild(GameObject().apply{
            transform.scale = Vector3(0.1f,0.1f,0.1f)
            transform.position = Vector3(0.1f,0.2f,0.1f)
            addComponent(SquareTest())
        })/*
        addChild(GameObject("cubes").apply{
            transform.position = Vector3(0f,0f,0.0f)
            addComponent(Mover())
            addChild(GameObject("cube").apply{
                transform.position = Vector3(-0.25f, -0.25f, -0.25f)
                transform.scale = Vector3(0.5f, 0.5f, 0.5f)
                addComponent(CubeRendererTest())
            })
            addChild(GameObject("cube").apply{
                transform.position = Vector3(0.25f, -0.25f, -0.25f)
                transform.scale = Vector3(0.5f, 0.5f, 0.5f)
                addComponent(CubeRendererTest())
            })
            addChild(GameObject("cube").apply{
                transform.position = Vector3(-0.25f, 0.25f, -0.25f)
                transform.scale = Vector3(0.5f, 0.5f, 0.5f)
                addComponent(CubeRendererTest())
            })
            addChild(GameObject("cube").apply{
                transform.position = Vector3(-0.25f, -0.25f, 0.25f)
                transform.scale = Vector3(0.5f, 0.5f, 0.5f)
                addComponent(CubeRendererTest())
            })
        })*/
        }
    }
}