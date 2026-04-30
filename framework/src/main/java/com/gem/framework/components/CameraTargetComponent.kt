package com.gem.framework.components

import com.gem.framework.Camera

class CameraTargetComponent(override var name: String = "Camera") : Component() {
    override fun onPostInit() {
        Camera.setTarget(gameObject)
    }
}
