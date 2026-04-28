package com.gem.framework.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.opengl.GLES20
import android.opengl.GLUtils
import com.gem.framework.*
import com.gem.framework.utils.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class TextComponent(
    text: String,
    val font: Font,
    fontSize: Float = 32f,
    color: Color = Color.Black
) : Component() {

    var text: String = text
        set(value) {
            if (field != value) {
                field = value
                if (initialized) updateTextTexture()
            }
        }

    var fontSize: Float = fontSize
        set(value) {
            if (field != value) {
                field = value
                if (initialized) updateTextTexture()
            }
        }

    var color: Color = color
        set(value) {
            if (field != value) {
                field = value
                if (initialized) updateTextTexture()
            }
        }

    private var textureId: Int = 0
    private val vertices = FloatArray(12)
    private val textureCoordinates = floatArrayOf(
        1.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 1.0f,
        0.0f, 0.0f
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
        updateTextTexture()
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)

        program = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
            checkProgramLink(this)
        }
    }

    private fun updateTextTexture() {
        val paint = Paint().apply {
            isAntiAlias = true
            textSize = this@TextComponent.fontSize
            color = this@TextComponent.color.toAndroidColor()
            typeface = font.typeface
        }

        val textWidth = paint.measureText(text).toInt().coerceAtLeast(1)
        val textHeight = (paint.descent() - paint.ascent()).toInt().coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888).apply {
            // Сохраняем «прямую» (не премультиплицированную) альфу — иначе при загрузке
            // в GL и blend SRC_ALPHA получим повторное умножение и тёмные/непрозрачные края.
            setHasAlpha(true)
            //setPremultiplied(false) // крашит приложение
        }
        val canvas = Canvas(bitmap)
        canvas.drawText(text, 0f, -paint.ascent(), paint)

        if (textureId != 0) {
            GLES20.glDeleteTextures(1, intArrayOf(textureId), 0)
            textureId = 0
        }

        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        textureId = textureIds[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()
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


        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        // Прозрачные объекты: включаем blend и НЕ пишем в depth-буфер (но читаем).
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glDepthMask(false)

        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer)

        val texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer)

        val textureHandle = GLES20.glGetUniformLocation(program, "uTexture")
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)

        // Возвращаем depth mask, чтобы не ломать остальные draw-call'ы кадра.
        GLES20.glDepthMask(true)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }

    override fun onRemove() {
        if (textureId != 0) {
            GLES20.glDeleteTextures(1, intArrayOf(textureId), 0)
            textureId = 0
        }
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
