package com.gem.framework.components

import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.common.Vec2
import com.gem.framework.*
import com.gem.framework.containers.PhysicsWorld
import com.gem.framework.utils.*

class RigidbodyComponent(
    private val bodyType: BodyType = BodyType.DYNAMIC
) : Component() {
    lateinit var body: Body
    private var lastTransform: Transform2D? = null
    lateinit var physicsWorld: PhysicsWorld

    override fun onPostInit() {
        var current: GameObject? = gameObject.parent
        while (current != null) {
            val pw = current.get<PhysicsWorld>()
            if (pw != null) {
                physicsWorld = pw
                physicsWorld.specialUpdatables.add(this)
                break
            }
            current = current.parent
        }
        if (!::physicsWorld.isInitialized) {
            throw ComponentException(
                "RigidbodyComponent: не найден PhysicsWorld выше по иерархии (объект '${gameObject.name}')"
            )
        }
        initializeBody()
    }

    override fun update() {
        if (::body.isInitialized) {
            gameObject.transform.globalPosition = Vector2(body.position.x, body.position.y)
            gameObject.transform.globalRotation = Math.toDegrees(body.angle.toDouble()).toFloat()
            lastTransform = gameObject.transform
        }
    }

    fun preUpdate() {
        if (::body.isInitialized) {
            val gp = gameObject.transform.globalPosition
            val gr = Math.toRadians(gameObject.transform.globalRotation.toDouble()).toFloat()
            if (
                body.position.x != gp.x ||
                body.position.y != gp.y ||
                body.angle != gr
            ) {
                body.setTransform(Vec2(gp.x, gp.y), gr)
                body.setAwake(true)
            }
        }
    }

    fun initializeBody() {
        val bodyDef = BodyDef()
        val gp = gameObject.transform.globalPosition
        bodyDef.position.set(gp.x, gp.y)
        bodyDef.angle = Math.toRadians(gameObject.transform.globalRotation.toDouble()).toFloat()
        bodyDef.type = bodyType
        body = physicsWorld.world.createBody(bodyDef)
    }

    fun addCollider(collider: ColliderComponent) {
        val fixtureDef = FixtureDef()
        fixtureDef.shape = collider.shape
        fixtureDef.density = collider.density
        fixtureDef.friction = collider.friction
        fixtureDef.restitution = collider.restitution
        collider.setFixtureData(body.createFixture(fixtureDef))
    }

    override fun onRemove() {
        if (::body.isInitialized) {
            physicsWorld.world.destroyBody(body)
        }
        if (::physicsWorld.isInitialized) {
            physicsWorld.specialUpdatables.remove(this)
        }
    }
}
