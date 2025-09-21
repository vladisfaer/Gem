package com.gem.framework.containers

import com.gem.framework.components.RigidbodyComponent
import org.jbox2d.dynamics.World
import org.jbox2d.common.Vec2
import com.gem.framework.components.ShotTest
import com.gem.framework.deltaTime

class PhysicsWorld(override var name: String = "PhysicsWorld") : Container(){
    val world = World(Vec2(0f, -9.8f))
    override fun rebuildUpdateOrder() {
        super.rebuildUpdateOrder()
        updateList = specialUpdatables.flatMap{
            listOf{(it as RigidbodyComponent).preUpdate()}
        } + {world.step(deltaTime, 8, 3)} + updateList
    }
}