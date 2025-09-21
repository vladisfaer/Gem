package com.gem.framework.components

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import com.gem.framework.utils.*
import com.gem.framework.*

class CircleComponent(col: Color = Color(1f, 0f, 1.0f, 1.0f), private val radius: Float = 0.5f, private val segments: Int = 100) : Component() {

    public var color = col
    private var vertices = FloatArray(segments * 3) // 3 компоненты на вершину

    private val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

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
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
        }
    }

    override fun update() {
        draw()
    }

    fun draw() {
        val transform = gameObject.transform

        // Расчет вершин круга с учетом радиуса и сегментов
        generateCircleVertices()

        // Преобразуем координаты вершин в экранные координаты
        val globMat = transform.globalMatrix()
        val screenVertices = mutableListOf<Vector2>()
        for (i in 0 until segments) {
            val x = vertices[i * 3]
            val y = vertices[i * 3 + 1]
            screenVertices.add(Camera.toScreenPosition(globMat.transform(Vector2(x, y))))
        }

        // Обновляем массив вершин с учётом глобальных координат
        vertices = FloatArray(segments * 3)
        for (i in 0 until segments) {
            val screenVertex = screenVertices[i]
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

    private fun generateCircleVertices() {
        val angleStep = 2 * Math.PI / segments
        var angle = 0.0

        // Вершины по кругу
        for (i in 0 until segments) {
            val x = (radius * Math.cos(angle)).toFloat()
            val y = (radius * Math.sin(angle)).toFloat()
            vertices[i * 3] = x
            vertices[i * 3 + 1] = y
            vertices[i * 3 + 2] = 0.0f
            angle += angleStep
        }
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

    override fun copy(): CircleComponent {
        return CircleComponent(color, radius, segments)
    }
}