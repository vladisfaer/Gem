package com.gem.framework

import org.jbox2d.dynamics.BodyType
import com.gem.framework.components.RectangleComponent
import com.gem.framework.utils.*
import com.gem.framework.components.*
import com.gem.framework.containers.*

object BuildScript {
    fun build(){
        rootObject.apply{
            add(GameObject("Coll Test").apply {
                transform.position = Vector2(0f,-3f)
                transform.scale = Vector2(3f,0.5f)
                transform.rotation = 10f
                add(TextComponent("HelloW", Font("Font.ttf"),50f,Color(0.5f,0.5f,0.5f,0.5f)))
                add(RigidbodyComponent(BodyType.STATIC))
                add(PolygonColliderComponent(Polygon("example.polygon")))
            })
            add(PhysicsWorld().apply {
                add(GameObject("Grav Test").apply {
                    transform.position = Vector2(0f,3f)
                    add(SpriteRendererComponent(Texture("bird.png")))
                    add(RigidbodyComponent())
                    add(CircleColliderComponent())
                })
            })
            add(EventBus())
            add(ShotTest())
            add(GameObject().apply{
                transform.scale = Vector2(0.5f,0.5f)
                transform.position = Vector2(0.2f,0.4f)
                add(RectangleComponent(col = Color(0f,0f,1f,0f)))
            })
            add(GameObject("CameraObject").apply{
                transform.scale = Vector2(5f,10f)
                add(CameraTargetComponent())
            })
        }
    }
}