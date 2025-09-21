package com.gem.framework.components

import org.jbox2d.dynamics.Fixture
import org.jbox2d.collision.shapes.Shape
import com.gem.framework.*

abstract class ColliderComponent(
    dens: Float = 1f,
    fric: Float = 0.5f,
    rest: Float = 0.0f
) : Component() {
    var density = dens
        set(value) {field = value; updateShape()}
    var friction = fric
        set(value) {field = value; updateShape()}
    var restitution = rest
        set(value) {field = value; updateShape()}

    lateinit var shape: Shape
    private var lastShape: Shape? = null
    var rigidbody: RigidbodyComponent? = null
    var fixture: Fixture? = null

    abstract fun initializeShape()

    fun updateShape() {
        initializeShape()
        if(shape != lastShape){
            fixture?.m_shape = shape
        }
        lastShape = shape
    }

    override fun update() {
        if (rigidbody == null) {
            findRigidbody(gameObject)
        } else {
            //updateShape()
        }
    }

    fun setFixtureData(infixture: Fixture){
        fixture = infixture
        fixture!!.userData = this
    }

    override fun onPostInit() {
        initializeShape()
        findRigidbody(gameObject)
    }

    private fun findRigidbody(gameObject: GameObject) {
        var parent: GameObject? = gameObject
        while (parent != null) {
            parent.get<RigidbodyComponent>()?.let {
                it.addCollider(this)
                fixture!!.userData = this
                rigidbody = it
                return
            }
            parent = parent.parent
        }
    }

    override fun onRemove() {
        rigidbody?.body?.let { body ->
            fixture?.let {
                body.destroyFixture(it)
                fixture = null
            }
        }
    }

    override fun updateCheck(): Boolean {
        return hasRigidbodyInHierarchy(gameObject)
    }

    private fun hasRigidbodyInHierarchy(gameObject: GameObject): Boolean {
        var current: GameObject? = gameObject
        while (current != null) {
            if (current.get<RigidbodyComponent>() != null) return true
            current = current.parent
        }
        return false
    }
}