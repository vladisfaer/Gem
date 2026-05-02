package com.gem.framework.components

import org.jbox2d.collision.shapes.*

class CircleColliderComponent(
    var radius: Float = 0.5f,
    density: Float = 1f,
    friction: Float = 0.5f,
    restitution: Float = 0.0f
) : ColliderComponent(density, friction, restitution) {

    override fun initializeShape() {
        val globalTransform = gameObject.transform
        val relativePosition = (rigidbody?.gameObject?.transform ?: gameObject.transform).globalPosition - gameObject.transform.globalPosition
        val globalRadius = radius * globalTransform.scale.x
        shape = CircleShape().apply {
            m_radius = globalRadius
            m_p.set(-relativePosition.x, -relativePosition.y)
        }
    }
}
