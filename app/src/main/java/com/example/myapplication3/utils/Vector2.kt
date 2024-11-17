package com.gem.framework.utils

import kotlin.math.*

data class Vector2(var x: Float = 0f, var y: Float = 0f) {

    // Операции с векторами
    operator fun plus(other: Vector2) = Vector2(x + other.x, y + other.y)
    operator fun minus(other: Vector2) = Vector2(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Vector2(x * scalar, y * scalar)
    operator fun div(scalar: Float) = Vector2(x / scalar, y / scalar)

    // Скалярное произведение двух векторов
    infix fun dot(other: Vector2): Float = x * other.x + y * other.y

    // Длина вектора
    val magnitude: Float
        get() = sqrt(x * x + y * y)

    // Нормализованный вектор (возвращает новый экземпляр)
    val normalized: Vector2
        get() = if (magnitude != 0f) Vector2(x / magnitude, y / magnitude) else Zero

    // Нормализация текущего вектора (изменяет текущий экземпляр)
    fun normalize() {
        val mag = magnitude
        if (mag != 0f) {
            x /= mag
            y /= mag
        }
    }

    // Поворот вектора на угол в градусах
    fun rotate(angleDegrees: Float): Vector2 {
        val radians = Math.toRadians(angleDegrees.toDouble())
        val cosA = cos(radians).toFloat()
        val sinA = sin(radians).toFloat()
        return Vector2(
            x * cosA - y * sinA,
            x * sinA + y * cosA
        )
    }

    companion object {
        val Zero = Vector2(0f, 0f)
        val One = Vector2(1f, 1f)
        val Up = Vector2(0f, 1f)
        val Down = Vector2(0f, -1f)
        val Left = Vector2(-1f, 0f)
        val Right = Vector2(1f, 0f)
    }
}