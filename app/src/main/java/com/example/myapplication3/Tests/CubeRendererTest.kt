/*package com.gem.framework.components

import android.opengl.GLES20
import com.gem.framework.GameObject
import com.gem.framework.utils.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class CubeRendererTest : Component() {

    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        uniform mat4 uMVPMatrix;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
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
        // Координаты вершин параллелепипеда
        val vertices = floatArrayOf(
            // Передняя грань
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            // Задняя грань
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f
        )

        // Цвета вершин
        val colors = floatArrayOf(
            1.0f, 0.0f, 0.0f, 1.0f, // Красный
            0.0f, 1.0f, 0.0f, 1.0f, // Зеленый
            0.0f, 0.0f, 1.0f, 1.0f, // Синий
            1.0f, 1.0f, 0.0f, 1.0f, // Желтый
            1.0f, 0.0f, 1.0f, 1.0f, // Фиолетовый
            0.0f, 1.0f, 1.0f, 1.0f, // Голубой
            1.0f, 1.0f, 1.0f, 1.0f, // Белый
            0.0f, 0.0f, 0.0f, 1.0f  // Черный
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
        val modelMatrix = transformToMatrix(gameObject.transform)

        // Активируем программу OpenGL
        GLES20.glUseProgram(program)

        // Загружаем матрицу
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelMatrix.values, 0)

        // Устанавливаем координаты вершин
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer)

        // Устанавливаем цвет
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4f(colorHandle, 1.0f, 0.0f, 0.0f, 1.0f) // Красный цвет для теста

        // Отрисовка параллелепипеда
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 24)

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
    
    override fun copy() : CubeRendererTest{
        return CubeRendererTest()
    }
}*/

/*
package com.gem.framework.components

import android.opengl.GLES20
import com.gem.framework.GameObject
import com.gem.framework.utils.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class CubeRendererTest : Component() {

    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        attribute vec4 vColor;
        uniform mat4 uMVPMatrix;
        varying vec4 fragColor;
        void main() {
            fragColor = vColor;
            gl_Position = uMVPMatrix * vPosition;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec4 fragColor;
        void main() {
            gl_FragColor = fragColor;
        }
    """

    private val program: Int

    init {
        // Координаты вершин параллелепипеда
        val vertices = floatArrayOf(
            // Передняя грань
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            // Задняя грань
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f
        )

        // Цвета для каждой вершины
        val colors = floatArrayOf(
            1.0f, 0.0f, 0.0f, 1.0f, // Красный
            0.0f, 1.0f, 0.0f, 1.0f, // Зеленый
            0.0f, 0.0f, 1.0f, 1.0f, // Синий
            1.0f, 1.0f, 0.0f, 1.0f, // Желтый
            1.0f, 0.0f, 1.0f, 1.0f, // Фиолетовый
            0.0f, 1.0f, 1.0f, 1.0f, // Голубой
            1.0f, 1.0f, 1.0f, 1.0f, // Белый
            0.0f, 0.0f, 0.0f, 1.0f  // Черный
        )

        // Индексы для отрисовки граней параллелепипеда
        val indices = shortArrayOf(
            0, 1, 2, 0, 2, 3,  // Передняя грань
            4, 5, 6, 4, 6, 7,  // Задняя грань
            0, 1, 5, 0, 5, 4,  // Нижняя грань
            3, 2, 6, 3, 6, 7,  // Верхняя грань
            0, 3, 7, 0, 7, 4,  // Левая грань
            1, 2, 6, 1, 6, 5   // Правая грань
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

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)

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
        val modelMatrix = transformToMatrix(gameObject.transform)

        // Активируем программу OpenGL
        GLES20.glUseProgram(program)

        // Загружаем матрицу
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelMatrix.values, 0)

        // Устанавливаем координаты вершин
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer)

        // Устанавливаем цвета вершин
        val colorHandle = GLES20.glGetAttribLocation(program, "vColor")
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer)

        // Отрисовка параллелепипеда
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        // Выключаем массивы вершин и цветов
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
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

    override fun copy(): CubeRendererTest {
        return CubeRendererTest()
    }
}*/


package com.gem.framework.components

import android.opengl.GLES20
import com.gem.framework.GameObject
import com.gem.framework.utils.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class CubeRendererTest : Component() {

    private val originalVertexBuffer: FloatBuffer
    private val transformedVertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        attribute vec4 vColor;
        varying vec4 fragColor;
        void main() {
            fragColor = vColor;
            gl_Position = vPosition;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec4 fragColor;
        void main() {
            gl_FragColor = fragColor;
        }
    """

    private val program: Int

    init {
        // Координаты вершин параллелепипеда
        val vertices = floatArrayOf(
            // Передняя грань
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            // Задняя грань
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f
        )

        // Цвета для каждой вершины
        val colors = floatArrayOf(
            1.0f, 0.0f, 0.0f, 1.0f, // Красный
            0.0f, 1.0f, 0.0f, 1.0f, // Зеленый
            0.0f, 0.0f, 1.0f, 1.0f, // Синий
            1.0f, 1.0f, 0.0f, 1.0f, // Желтый
            1.0f, 0.0f, 1.0f, 1.0f, // Фиолетовый
            0.0f, 1.0f, 1.0f, 1.0f, // Голубой
            1.0f, 1.0f, 1.0f, 1.0f, // Белый
            0.0f, 0.0f, 0.0f, 1.0f  // Черный
        )

        // Индексы для отрисовки граней параллелепипеда
        val indices = shortArrayOf(
            0, 1, 2, 0, 2, 3,  // Передняя грань
            4, 5, 6, 4, 6, 7,  // Задняя грань
            0, 1, 5, 0, 5, 4,  // Нижняя грань
            3, 2, 6, 3, 6, 7,  // Верхняя грань
            0, 3, 7, 0, 7, 4,  // Левая грань
            1, 2, 6, 1, 6, 5   // Правая грань
        )

        // Инициализация оригинальных и трансформированных буферов
        originalVertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        originalVertexBuffer.put(vertices)
        originalVertexBuffer.position(0)

        transformedVertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        colorBuffer = ByteBuffer.allocateDirect(colors.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        colorBuffer.put(colors)
        colorBuffer.position(0)

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)

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
        val modelMatrix = gameObject.transform.globalMatrix()

        // Обновление трансформированных позиций
        updateTransformedVertices(modelMatrix)

        // Активируем программу OpenGL
        GLES20.glUseProgram(program)

        // Устанавливаем координаты вершин
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, transformedVertexBuffer)

        // Устанавливаем цвета вершин
        val colorHandle = GLES20.glGetAttribLocation(program, "vColor")
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer)

        // Отрисовка параллелепипеда
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        // Выключаем массивы вершин и цветов
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }

    private fun updateTransformedVertices(modelMatrix: Matrix4) {
        transformedVertexBuffer.clear()
        for (i in 0 until originalVertexBuffer.limit() / 3) {
            val x = originalVertexBuffer.get(i * 3)
            val y = originalVertexBuffer.get(i * 3 + 1)
            val z = originalVertexBuffer.get(i * 3 + 2)
            val transformed = modelMatrix.transform(Vector3(x, y, z))
            transformedVertexBuffer.put(transformed.x)
            transformedVertexBuffer.put(transformed.y)
            transformedVertexBuffer.put(transformed.z)
        }
        transformedVertexBuffer.position(0)
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

    override fun copy(): CubeRendererTest {
        return CubeRendererTest()
    }
}