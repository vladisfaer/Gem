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
//    public val colliders = mutableListOf<ColliderComponent>()
    lateinit var physicsWorld: PhysicsWorld

    override fun onPostInit() {
        var current: GameObject? = gameObject.parent
        while (current != null) {
            if (current.get<PhysicsWorld>() != null){
                physicsWorld = current.get<PhysicsWorld>() as PhysicsWorld
                physicsWorld.specialUpdatables.add(this)
                break
            }
            current = current.parent
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

//    override fun updateCheck(): Boolean {
//        return !hasRigidbodyInHierarchy(gameObject)
//    }

    fun preUpdate() {
        if (::body.isInitialized) {
            if (
                (body.position.x != gameObject.transform.globalPosition.x) or
                (body.position.y != gameObject.transform.globalPosition.y) or
                (body.angle != Math.toRadians(gameObject.transform.globalRotation.toDouble()).toFloat())
            ){
                body.setTransform(
                    Vec2(
                        gameObject.transform.globalPosition.x,
                        gameObject.transform.globalPosition.y
                    ),
                    Math.toRadians(gameObject.transform.globalRotation.toDouble()).toFloat()
                )
                body.setAwake(true)
            }
        }
    }

    fun initializeBody() {
        val bodyDef = BodyDef()
        bodyDef.position.set(gameObject.transform.position.x, gameObject.transform.globalPosition.y)
        bodyDef.angle = Math.toRadians(gameObject.transform.rotation.toDouble()).toFloat()
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
    }

    private fun hasRigidbodyInHierarchy(gameObject: GameObject): Boolean {
        var current: GameObject? = gameObject.parent
        while (current != null) {
            if (current.get<RigidbodyComponent>() != null) return true
            current = current.parent
        }
        return hasRigidbodyInDescendants(gameObject)
    }

    private fun hasRigidbodyInDescendants(gameObject: GameObject): Boolean {
        if (gameObject.get<RigidbodyComponent>() != null) return true
        gameObject.getChildren().forEach() {
            hasRigidbodyInDescendants(it)
        }
        return false
    }

    override fun copy(): RigidbodyComponent {
        return RigidbodyComponent()
    }
}