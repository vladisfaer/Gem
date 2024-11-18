package com.gem.framework

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

val rootObject: GameObject = GameObject("root")
var time: Float = 0f
class GLRenderer : GLSurfaceView.Renderer {
    private var initialized = false
    private var canUpdate = false
    private val startTime = System.nanoTime()
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        println("New surface")
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glClearColor(1f, 1f, 1f, 1f)
        
        BuildScript.build()
        rootObject.postInit()
        
        initialized = true
    }

    override fun onDrawFrame(gl: GL10?) {
        time = (System.nanoTime() - startTime) / 1_000_000_000f
        if (initialized && !canUpdate) {
            canUpdate = true
        }
        if (canUpdate) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            rootObject.tryUpdateAll()
        } else {
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }
}