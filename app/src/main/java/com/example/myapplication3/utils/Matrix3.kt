package com.gem.framework.utils

import kotlin.math.*

class Matrix3(
    val values: FloatArray = floatArrayOf(
        1f, 0f, 0f,  // Первая строка
        0f, 1f, 0f,  // Вторая строка
        0f, 0f, 1f   // Третья строка
    )
) {
    // Умножение матрицы на другую матрицу
    operator fun times(other: Matrix3): Matrix3 {
        val result = FloatArray(9)
        for (row in 0..2) {
            for (col in 0..2) {
                result[row * 3 + col] = (values[row * 3] * other.values[col] +
                        values[row * 3 + 1] * other.values[col + 3] +
                        values[row * 3 + 2] * other.values[col + 6])
            }
        }
        return Matrix3(result)
    }

    // Умножение матрицы на скаляр
    operator fun times(scalar: Float): Matrix3 {
        return Matrix3(values.map { it * scalar }.toFloatArray())
    }

    // Деление матрицы на скаляр
    operator fun div(scalar: Float): Matrix3 {
        return Matrix3(values.map { it / scalar }.toFloatArray())
    }

    // Сложение матриц
    operator fun plus(other: Matrix3): Matrix3 {
        return Matrix3(values.zip(other.values) { a, b -> a + b }.toFloatArray())
    }

    // Вычитание матриц
    operator fun minus(other: Matrix3): Matrix3 {
        return Matrix3(values.zip(other.values) { a, b -> a - b }.toFloatArray())
    }

    // Трансформация вектора
    fun transform(v: Vector2): Vector2 {
        val x = v.x * values[0] + v.y * values[1] + values[2]
        val y = v.x * values[3] + v.y * values[4] + values[5]
        return Vector2(x, y)
    }

    // Обратная матрица
    fun invert(): Matrix3 {
        val det = determinant()
        if (det == 0f) throw IllegalArgumentException("Матрица необратима")
        val invDet = 1f / det
        val result = FloatArray(9)
        result[0] = values[4] * values[8] - values[5] * values[7]
        result[1] = values[2] * values[7] - values[1] * values[8]
        result[2] = values[1] * values[5] - values[2] * values[4]
        result[3] = values[5] * values[6] - values[3] * values[8]
        result[4] = values[0] * values[8] - values[2] * values[6]
        result[5] = values[2] * values[3] - values[0] * values[5]
        result[6] = values[3] * values[7] - values[4] * values[6]
        result[7] = values[1] * values[6] - values[0] * values[7]
        result[8] = values[0] * values[4] - values[1] * values[3]
        return Matrix3(result.map { it * invDet }.toFloatArray())
    }

    // Определитель матрицы
    fun determinant(): Float {
        return values[0] * (values[4] * values[8] - values[5] * values[7]) -
               values[1] * (values[3] * values[8] - values[5] * values[6]) +
               values[2] * (values[3] * values[7] - values[4] * values[6])
    }

    // Транспонирование матрицы
    fun transpose(): Matrix3 {
        return Matrix3(floatArrayOf(
            values[0], values[3], values[6],
            values[1], values[4], values[7],
            values[2], values[5], values[8]
        ))
    }

    // Создание матрицы сдвига
    fun translate(dx: Float, dy: Float): Matrix3 {
        val translationMatrix = Matrix3(floatArrayOf(
            1f, 0f, dx,
            0f, 1f, dy,
            0f, 0f, 1f
        ))
        return this * translationMatrix
    }

    // Создание матрицы масштабирования
    fun scale(sx: Float, sy: Float): Matrix3 {
        val scalingMatrix = Matrix3(floatArrayOf(
            sx, 0f, 0f,
            0f, sy, 0f,
            0f, 0f, 1f
        ))
        return this * scalingMatrix
    }

    // Создание матрицы поворота
    fun rotate(angleDegrees: Float): Matrix3 {
        val radians = Math.toRadians(angleDegrees.toDouble())
        val cosA = cos(radians).toFloat()
        val sinA = sin(radians).toFloat()
        val rotationMatrix = Matrix3(floatArrayOf(
            cosA, -sinA, 0f,
            sinA, cosA, 0f,
            0f, 0f, 1f
        ))
        return this * rotationMatrix
    }

    companion object {
        fun identity() = Matrix3()
    }
}