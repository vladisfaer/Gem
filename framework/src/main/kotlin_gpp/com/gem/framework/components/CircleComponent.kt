package com.gem.framework.components

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import com.gem.framework.utils.*
import com.gem.framework.*

class CircleComponent(
    var color: Color = Color(1f, 0f, 1.0f, 1.0f),
    private val radius: Float = 0.5f,
    private val segments: Int = 100
) : Component() {

    @DontSave private val localVertices: FloatArray = FloatArray(segments * 3)
    @DontSave private val vertices: FloatArray = FloatArray(segments * 3)

    @DontSave private val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    @DontSave private val program: Int

    init {
        generateLocalVertices()
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)

        program = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
            checkProgramLink(this)
        }
    }

    override fun update() {
        draw()
    }

    fun draw() {
        val transform = gameObject.transform
        val globMat = transform.globalMatrix()

        val tmp = Vector2(0f, 0f)
        for (i in 0 until segments) {
            tmp.x = localVertices[i * 3]
            tmp.y = localVertices[i * 3 + 1]
            val screenVertex = Camera.worldToViewport(globMat.transform(tmp))
            vertices[i * 3] = screenVertex.x
            vertices[i * 3 + 1] = screenVertex.y
            vertices[i * 3 + 2] = 0.0f
        }

        vertexBuffer.clear()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        GLES20.glUseProgram(program)
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer)
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(color.r, color.g, color.b, color.a), 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, segments)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun generateLocalVertices() {
        val angleStep = 2 * Math.PI / segments
        var angle = 0.0
        for (i in 0 until segments) {
            val x = (radius * Math.cos(angle)).toFloat()
            val y = (radius * Math.sin(angle)).toFloat()
            localVertices[i * 3] = x
            localVertices[i * 3 + 1] = y
            localVertices[i * 3 + 2] = 0.0f
            angle += angleStep
        }
    }

    override fun onRemove() {
        if (program != 0) {
            GLES20.glDeleteProgram(program)
        }
    }

    companion object {
        private const val VERTEX_SHADER_CODE = """
            attribute vec4 vPosition;
            void main() {
                gl_Position = vPosition;
            }
        """

        private const val FRAGMENT_SHADER_CODE = """
            precision mediump float;
            uniform vec4 vColor;
            void main() {
                gl_FragColor = vColor;
            }
        """

        private fun loadShader(type: Int, shaderCode: String): Int {
            return GLES20.glCreateShader(type).also { shader ->
                GLES20.glShaderSource(shader, shaderCode)
                GLES20.glCompileShader(shader)
                val compiled = IntArray(1)
                GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
                if (compiled[0] == 0) {
                    val log = GLES20.glGetShaderInfoLog(shader)
                    GLES20.glDeleteShader(shader)
                    throw ShaderCompilationException("Ошибка компиляции шейдера: $log")
                }
            }
        }

        private fun checkProgramLink(program: Int) {
            val linked = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linked, 0)
            if (linked[0] == 0) {
                val log = GLES20.glGetProgramInfoLog(program)
                GLES20.glDeleteProgram(program)
                throw ShaderCompilationException("Ошибка линковки шейдерной программы: $log")
            }
        }
    }
}
