package com.gem.framework.components

import com.gem.framework.*

class TreeAnalyzerComponent(override var name: String = "Analyzer") : Component() {
    var mtime = 0f

    override fun update() {
        if (time < mtime + 2f) return
        println(time)
        //println("updates: "+rootObject.updateList.size.toString())
        printTree(rootObject)
        mtime = time
    }
    
    fun printTree(mobj: GameObject, depth: Int = 0) {
        mobj.updatables.forEach{
            if (it is GameObject) {
                println("-".repeat(depth)+"\\ " + it.name)
                printTree(it,depth+1)
            } else {
                println("-".repeat(depth)+" "+it.name)
            }
        }
    }
}
