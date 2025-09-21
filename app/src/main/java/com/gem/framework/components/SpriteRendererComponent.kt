package com.gem.framework.components

import android.opengl.GLES20
import com.gem.framework.*
import com.gem.framework.utils.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class SpriteRendererComponent(private var texture: Texture) : Component() {

    private var vertices = FloatArray(12)
    private var textureCoordinates = FloatArray(8)

    private val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    private val textureBuffer: FloatBuffer = ByteBuffer.allocateDirect(textureCoordinates.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord;
        void main() {
            gl_Position = vPosition;
            vTexCoord = aTexCoord;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec2 vTexCoord;
        uniform sampler2D uTexture;
        void main() {
            gl_FragColor = texture2D(uTexture, vTexCoord);
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

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
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

        val screenTopRight = Camera.toScreenPosition(globMat.transform(topRight))
        val screenBottomRight = Camera.toScreenPosition(globMat.transform(bottomRight))
        val screenBottomLeft = Camera.toScreenPosition(globMat.transform(bottomLeft))
        val screenTopLeft = Camera.toScreenPosition(globMat.transform(topLeft))

        vertices = floatArrayOf(
            screenTopRight.x, screenTopRight.y, 0.0f,
            screenBottomRight.x, screenBottomRight.y, 0.0f,
            screenBottomLeft.x, screenBottomLeft.y, 0.0f,
            screenTopLeft.x, screenTopLeft.y, 0.0f
        )

        textureCoordinates = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
        )

        vertexBuffer.clear()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        textureBuffer.clear()
        textureBuffer.put(textureCoordinates)
        textureBuffer.position(0)

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

    override fun copy(): SpriteRendererComponent {
        return SpriteRendererComponent(texture)
    }
}