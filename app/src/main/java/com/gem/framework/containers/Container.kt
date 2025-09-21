package com.gem.framework.containers

import com.gem.framework.GameObject
import com.gem.framework.components.Updatable

abstract class Container(override var name: String = "Container") : GameObject() {
    val specialUpdatables: MutableList<Updatable> = mutableListOf()
}