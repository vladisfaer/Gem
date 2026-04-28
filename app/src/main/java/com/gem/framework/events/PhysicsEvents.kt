package com.gem.framework.events

import com.gem.framework.GameObject
import com.gem.framework.utils.Vector2

abstract class PhysicsEvent : EventData()

class CollisionBeginEvent(
    val a: GameObject,
    val b: GameObject,
    val contactPoint: Vector2? = null
) : PhysicsEvent()

class CollisionEndEvent(
    val a: GameObject,
    val b: GameObject
) : PhysicsEvent()
