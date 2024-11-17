package com.gem.framework.utils

import kotlin.math.*

class Matrix4(
    val values: FloatArray = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )
) {
    // Умножение матриц
    operator fun times(other: Matrix4): Matrix4 {
        val result = FloatArray(16)
        for (row in 0..3) {
            for (col in 0..3) {
                result[row * 4 + col] = (values[row * 4] * other.values[col] +
                        values[row * 4 + 1] * other.values[col + 4] +
                        values[row * 4 + 2] * other.values[col + 8] +
                        values[row * 4 + 3] * other.values[col + 12])
            }
        }
        return Matrix4(result)
    }

    // Умножение на скаляр
    fun scalarMultiply(scalar: Float): Matrix4 {
        return Matrix4(values.map { it * scalar }.toFloatArray())
    }

    // Деление на скаляр
    operator fun div(scalar: Float): Matrix4 {
        return Matrix4(values.map { it / scalar }.toFloatArray())
    }

    // Сложение матриц
    operator fun plus(other: Matrix4): Matrix4 {
        return Matrix4(values.zip(other.values) { a, b -> a + b }.toFloatArray())
    }

    // Вычитание матриц
    operator fun minus(other: Matrix4): Matrix4 {
        return Matrix4(values.zip(other.values) { a, b -> a - b }.toFloatArray())
    }

    // Трансформация 3D-вектора
    fun transform(v: Vector3): Vector3 {
        val x = v.x * values[0] + v.y * values[1] + v.z * values[2] + values[3]
        val y = v.x * values[4] + v.y * values[5] + v.z * values[6] + values[7]
        val z = v.x * values[8] + v.y * values[9] + v.z * values[10] + values[11]
        return Vector3(x, y, z)
    }

    // Обратная матрица
    fun invert(): Matrix4 {
        val inv = FloatArray(16) { 0f }
        val m = values
        val temp = FloatArray(16) { 0f }
        
        // Расширяем матрицу: [M | I]
        for (i in 0..3) {
            for (j in 0..3) {
                temp[i * 4 + j] = m[i * 4 + j]
                inv[i * 4 + j] = if (i == j) 1f else 0f
            }
        }

        // Приведение к единичной матрице
        for (i in 0..3) {
            var pivot = temp[i * 4 + i]
            if (pivot == 0f) throw IllegalArgumentException("Матрица необратима")

            // Нормализация строки
            for (j in 0..3) {
                temp[i * 4 + j] /= pivot
                inv[i * 4 + j] /= pivot
            }

            // Обнуление других строк
            for (k in 0..3) {
                if (k != i) {
                    val factor = temp[k * 4 + i]
                    for (j in 0..3) {
                        temp[k * 4 + j] -= factor * temp[i * 4 + j]
                        inv[k * 4 + j] -= factor * inv[i * 4 + j]
                    }
                }
            }
        }

        return Matrix4(inv)
    }

    // Определитель матрицы
    fun determinant(): Float {
        return values[0] * (values[5] * (values[10] * values[15] - values[11] * values[14]) -
                            values[9] * (values[6] * values[15] - values[7] * values[14]) +
                            values[13] * (values[6] * values[11] - values[7] * values[10]))
        // Расчет других элементов определителя аналогично...
    }

    // Транспонирование
    fun transpose(): Matrix4 {
        return Matrix4(floatArrayOf(
            values[0], values[4], values[8], values[12],
            values[1], values[5], values[9], values[13],
            values[2], values[6], values[10], values[14],
            values[3], values[7], values[11], values[15]
        ))
    }

    // Матрица сдвига
    fun translate(delta: Vector3): Matrix4 {
        val dx = delta.x
        val dy = delta.y
        val dz = delta.z
        val translationMatrix = Matrix4(floatArrayOf(
            1f, 0f, 0f, dx,
            0f, 1f, 0f, dy,
            0f, 0f, 1f, dz,
            0f, 0f, 0f, 1f
        ))
        return this * translationMatrix
    }

    // Матрица масштабирования
    fun scale(scale: Vector3): Matrix4 {
        val sx = scale.x
        val sy = scale.y
        val sz = scale.z
        val scalingMatrix = Matrix4(floatArrayOf(
            sx, 0f, 0f, 0f,
            0f, sy, 0f, 0f,
            0f, 0f, sz, 0f,
            0f, 0f, 0f, 1f
        ))
        return this * scalingMatrix
    }

    fun rotateX(angleDegrees: Float): Matrix4 {
        val radians = Math.toRadians(angleDegrees.toDouble())
        val cosA = cos(radians).toFloat()
        val sinA = sin(radians).toFloat()
        val rotationMatrix = Matrix4(floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, cosA, -sinA, 0f,
            0f, sinA, cosA, 0f,
            0f, 0f, 0f, 1f
        ))
        return this * rotationMatrix
    }
    
    fun rotateY(angleDegrees: Float): Matrix4 {
        val radians = Math.toRadians(angleDegrees.toDouble())
        val cosA = cos(radians).toFloat()
        val sinA = sin(radians).toFloat()
        val rotationMatrix = Matrix4(floatArrayOf(
            cosA, 0f, sinA, 0f,
            0f, 1f, 0f, 0f,
            -sinA, 0f, cosA, 0f,
            0f, 0f, 0f, 1f
        ))
        return this * rotationMatrix
    }
    
    fun rotateZ(angleDegrees: Float): Matrix4 {
        val radians = Math.toRadians(angleDegrees.toDouble())
        val cosA = cos(radians).toFloat()
        val sinA = sin(radians).toFloat()
        val rotationMatrix = Matrix4(floatArrayOf(
            cosA, -sinA, 0f, 0f,
            sinA, cosA, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        ))
        return this * rotationMatrix
    }
    
    // Перспективная проекция
    fun perspective(fov: Float, aspectRatio: Float, near: Float, far: Float): Matrix4 {
        val f = 1f / tan(Math.toRadians(fov / 2.0).toFloat())
        val range = near - far
        return Matrix4(floatArrayOf(
            f / aspectRatio, 0f, 0f, 0f,
            0f, f, 0f, 0f,
            0f, 0f, (far + near) / range, 2f * far * near / range,
            0f, 0f, -1f, 0f
        ))
    }

    // Матрица вида
    fun lookAt(eye: Vector3, center: Vector3, up: Vector3): Matrix4 {
        val f = (center - eye).normalized
        val s = f.cross(up).normalized
        val u = s.cross(f)

        return Matrix4(floatArrayOf(
            s.x, u.x, -f.x, 0f,
            s.y, u.y, -f.y, 0f,
            s.z, u.z, -f.z, 0f,
            -s.dot(eye), -u.dot(eye), f.dot(eye), 1f
        ))
    }

    companion object {
        fun identity() = Matrix4()
    }
    
    fun rotateEuler(eulerAngles: Vector3): Matrix4 {
        // Разделяем углы на X, Y, Z
        val (x, y, z) = eulerAngles

        // Матрицы вращения вокруг каждой оси
        val rotationX = Matrix4(floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, cos(x), -sin(x), 0f,
            0f, sin(x), cos(x), 0f,
            0f, 0f, 0f, 1f
        ))

        val rotationY = Matrix4(floatArrayOf(
            cos(y), 0f, sin(y), 0f,
            0f, 1f, 0f, 0f,
            -sin(y), 0f, cos(y), 0f,
            0f, 0f, 0f, 1f
        ))

        val rotationZ = Matrix4(floatArrayOf(
            cos(z), -sin(z), 0f, 0f,
            sin(z), cos(z), 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        ))

        // Умножаем матрицы вращения (Rz * Ry * Rx)
        return this * (rotationZ) * (rotationY) * (rotationX)
    }
}