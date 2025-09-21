package com.gem.framework.components

import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2

class BoxColliderComponent(
    var width: Float = 1f,
    var height: Float = 1f,
    density: Float = 1f,
    friction: Float = 0.5f,
    restitution: Float = 0.0f
) : ColliderComponent(density, friction, restitution) {

    override fun initializeShape() {
        val globalTransform = gameObject.transform
        val relativePosition = (rigidbody?.gameObject?.transform
            ?: gameObject.transform).globalPosition - gameObject.transform.globalPosition
        val relativeRotation = (rigidbody?.gameObject?.transform
            ?: gameObject.transform).globalRotation - gameObject.transform.globalRotation

        val globalWidth = width * globalTransform.scale.x
        val globalHeight = height * globalTransform.scale.y
        val globalOffsetX = relativePosition.x
        val globalOffsetY = relativePosition.y
        val globalRotationRadians = Math.toRadians(relativeRotation.toDouble()).toFloat()

        shape = PolygonShape().apply {
            setAsBox(
                globalWidth / 2,
                globalHeight / 2,
                Vec2(-globalOffsetX, -globalOffsetY),
                -globalRotationRadians
            )
        }
    }

    override fun copy(): BoxColliderComponent {
        return BoxColliderComponent(width, height, density, friction, restitution)
    }
}