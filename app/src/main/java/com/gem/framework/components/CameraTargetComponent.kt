package com.gem.framework.components

import com.gem.framework.Camera

public class CameraTargetComponent(override var name: String = "Camera") : Component() {
    override fun onPostInit(){
        Camera.setTarget(gameObject)
    }

    override fun copy(): Component {
        return CameraTargetComponent()
    }
}
