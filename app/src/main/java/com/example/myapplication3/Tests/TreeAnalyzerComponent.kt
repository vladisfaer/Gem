package com.example.myapplication3.components

import com.gem.framework.components.Component
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