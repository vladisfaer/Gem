package com.gem.framework.components

import android.opengl.GLES20
import com.gem.framework.Camera
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import com.gem.framework.utils.*

class RectangleComponent(
    override var name: String = "graphics",
    var color: Color = Color(1f, 0f, 1.0f, 0.125f)
) : Component() {

    @Transient private var vertices = FloatArray(12)

    @Transient private val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    @Transient private val program: Int

    init {
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

        val halfWidth = 0.5f
        val halfHeight = 0.5f

        val topRight = Vector2(halfWidth, halfHeight)
        val bottomRight = Vector2(halfWidth, -halfHeight)
        val bottomLeft = Vector2(-halfWidth, -halfHeight)
        val topLeft = Vector2(-halfWidth, halfHeight)

        val globMat = transform.globalMatrix()

        val screenTopRight = Camera.worldToViewport(globMat.transform(topRight))
        val screenBottomRight = Camera.worldToViewport(globMat.transform(bottomRight))
        val screenBottomLeft = Camera.worldToViewport(globMat.transform(bottomLeft))
        val screenTopLeft = Camera.worldToViewport(globMat.transform(topLeft))

        vertices[0] = screenTopRight.x;    vertices[1]  = screenTopRight.y;    vertices[2]  = 0.0f
        vertices[3] = screenBottomRight.x; vertices[4]  = screenBottomRight.y; vertices[5]  = 0.0f
        vertices[6] = screenBottomLeft.x;  vertices[7]  = screenBottomLeft.y;  vertices[8]  = 0.0f
        vertices[9] = screenTopLeft.x;     vertices[10] = screenTopLeft.y;     vertices[11] = 0.0f

        vertexBuffer.clear()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        GLES20.glUseProgram(program)
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer)
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(color.r, color.g, color.b, color.a), 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)
        GLES20.glDisableVertexAttribArray(positionHandle)
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
