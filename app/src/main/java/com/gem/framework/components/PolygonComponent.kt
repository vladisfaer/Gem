package com.gem.framework.components

import android.opengl.GLES20
import com.gem.framework.utils.*
import com.gem.framework.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class PolygonComponent(private val polygon: Polygon, col: Color = Color(1f, 1f, 1f, 1f)) : Component() {

    private var color = col
    private val vertices = FloatArray(polygon.vertices.size * 3)
    private val vertexBuffer: FloatBuffer

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        void main() {
            gl_Position = vPosition;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """

    private val program: Int

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
        }
    }

    override fun update() {
        updateVertices()
        draw()
    }

    private fun updateVertices() {
        val transform = gameObject.transform
        val globalMatrix = transform.globalMatrix()

        polygon.vertices.forEachIndexed { index, localVertex ->
            val transformedVertex = Camera.toScreenPosition(globalMatrix.transform(localVertex))
            vertices[index * 3] = transformedVertex.x  // x
            vertices[index * 3 + 1] = transformedVertex.y  // y
            vertices[index * 3 + 2] = 0.0f  // z
        }

        vertexBuffer.clear()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)
    }

    private fun draw() {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer)

        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(color.r, color.g, color.b, color.a), 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, polygon.vertices.size)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                GLES20.glDeleteShader(shader)
                throw RuntimeException("Ошибка компиляции шейдера: ${GLES20.glGetShaderInfoLog(shader)}")
            }
        }
    }

    override fun copy(): PolygonComponent {
        return PolygonComponent(polygon, col = color)
    }
}