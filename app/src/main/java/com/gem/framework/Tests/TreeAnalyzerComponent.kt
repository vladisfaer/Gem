package com.gem.framework.components

import com.gem.framework.time

class TreeAnalyzerComponent(override var name: String = "Analyzer"): Component() {
    var mtime = 0f

    override fun update() {
        if(time < mtime + 1f){return}
        println(gameObject.transform.position)
        mtime = time
    }

    override fun copy(): TreeAnalyzerComponent { return TreeAnalyzerComponent() }
}