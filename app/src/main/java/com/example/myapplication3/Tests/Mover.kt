package com.gem.framework.components

import com.gem.framework.utils.Vector3
import com.gem.framework.time

public class Mover : Component(){
    
    override fun update() {
        gameObject.transform.rotation = Vector3(time * 0.8f,time * 1f,time * 0f)
        //gameObject.transform.position = Vector3(0.01f * time, 0.1f, 1f)
    }
    
    override fun copy() : Mover{
        return Mover()
    }
}
