package com.gem.framework.components

import com.gem.framework.time

class AutoDesComponent(override var name: String = "autodes") : Component() {
    var mtime = 0f

    override fun onPostInit() {
        mtime = time
    }

    override fun update() {
        val dtime = time - mtime
        if (10 < dtime && dtime < 15) {
            gameObject.destroy()
        }
    }
}
