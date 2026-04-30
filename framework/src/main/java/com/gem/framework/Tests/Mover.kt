package com.gem.framework.components

import com.gem.framework.time

class Mover : Component() {

    override fun update() {
        gameObject.transform.rotation = time * 90f
    }
}
