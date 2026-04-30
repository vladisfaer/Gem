package com.gem.framework.components

import android.opengl.GLES20
import com.gem.framework.*
import com.gem.framework.utils.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class SpriteRendererComponent(private var texture: Texture) : Component() {

    private val vertices = FloatArray(12)
    private val textureCoordinates = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    )

    private val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    private val textureBuffer: FloatBuffer = ByteBuffer.allocateDirect(textureCoordinates.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer().apply {
            put(textureCoordinates)
            position(0)
        }

    private val program: Int

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

        // Прозрачность включаем перед каждой отрисовкой — глобальное состояние GL ненадёжно.
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer)

        val texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer)

        val textureHandle = GLES20.glGetUniformLocation(program, "uTexture")
        GLES20.glUniform1i(textureHandle, 0)

        texture.bind()

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    override fun onRemove() {
        if (program != 0) {
            GLES20.glDeleteProgram(program)
        }
    }

    companion object {
        private const val VERTEX_SHADER_CODE = """
            attribute vec4 vPosition;
            attribute vec2 aTexCoord;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = vPosition;
                vTexCoord = aTexCoord;
            }
        """

        private const val FRAGMENT_SHADER_CODE = """
            precision mediump float;
            varying vec2 vTexCoord;
            uniform sampler2D uTexture;
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord);
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
