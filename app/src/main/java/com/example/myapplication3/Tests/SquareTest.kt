package com.gem.framework.components

import android.opengl.GLES20
import com.gem.framework.GameObject
import com.gem.framework.utils.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class SquareTest : Component() {

    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer

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
        // Координаты вершин прямоугольника
        val vertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f,  // Нижний левый угол
            0.5f, -0.5f, 0.0f,   // Нижний правый угол
            0.5f, 0.5f, 0.0f,    // Верхний правый угол
            -0.5f, 0.5f, 0.0f    // Верхний левый угол
        )

        // Цвета вершин
        val colors = floatArrayOf(
            1.0f, 0.0f, 0.0f, 1.0f, // Красный
            0.0f, 1.0f, 0.0f, 1.0f, // Зеленый
            0.0f, 0.0f, 1.0f, 1.0f, // Синий
            1.0f, 1.0f, 0.0f, 1.0f  // Желтый
        )

        // Инициализация буферов
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        colorBuffer = ByteBuffer.allocateDirect(colors.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        colorBuffer.put(colors)
        colorBuffer.position(0)

        // Компиляция шейдеров
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // Создание OpenGL-программы
        program = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
        }
    }

    override fun update() {
        draw()
    }

    private fun draw() {
        // Матрица модели, полученная из Transform
        //val modelMatrix = transformToMatrix(gameObject.transform)

        // Активируем программу OpenGL
        GLES20.glUseProgram(program)

        // Загружаем матрицу
        //val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        //GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelMatrix.values, 0)

        // Устанавливаем координаты вершин
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer)

        // Устанавливаем цвет
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4f(colorHandle, 1.0f, 0.5f, 0.0f, 1.0f) // Оранжевый цвет для теста

        // Отрисовка прямоугольника (2 треугольника)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        // Выключаем массив вершин
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun transformToMatrix(transform: Transform): Matrix4 {
        val translation = Matrix4().translate(transform.globalPosition)
        val rotation = Matrix4().rotateEuler(transform.globalRotation)
        val scaling = Matrix4().scale(transform.globalScale)
        return translation * rotation * scaling
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
    
    override fun copy() : SquareTest { return SquareTest() }
}