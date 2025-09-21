package com.gem.framework.components

import com.gem.framework.time

class AutoDesComponent(override var name: String = "autodes"): Component() {
    var mtime = 0f

    override fun onPostInit() {
        mtime = time + 5f
    }

    override fun update() {
        if(time > mtime){
            gameObject.destroy()
        }
    }

    override fun copy(): Component {
        return AutoDesComponent()
    }
}