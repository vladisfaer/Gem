package com.gem.framework.components

import com.gem.framework.GameObject
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import com.gem.framework.utils.*

class PolygonColliderComponent(
    val polygon: Polygon,
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
        val relativeTransform = GameObject().transform.apply {
            position = relativePosition
            rotation = relativeRotation
            scale = globalTransform.scale
        }

        shape = PolygonShape().apply {
            val transformedVertices = polygon.getVec2Array().map {
                val transformedPoint = relativeTransform.globalMatrix().transform(Vector2(it.x, it.y))
                Vec2(transformedPoint.x.toFloat(), transformedPoint.y.toFloat())
            }
            set(transformedVertices.toTypedArray(), transformedVertices.size)
        }
    }
}
