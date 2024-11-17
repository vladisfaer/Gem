package com.gem.framework.utils

import kotlin.math.*

data class Vector3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {

    // Операции с векторами
    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Vector3(x * scalar, y * scalar, z * scalar)
    operator fun div(scalar: Float) = Vector3(x / scalar, y / scalar, z / scalar)

    // Скалярное произведение двух векторов
    infix fun dot(other: Vector3): Float = x * other.x + y * other.y + z * other.z

    // Векторное произведение
    infix fun cross(other: Vector3) = Vector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

    // Длина вектора
    val magnitude: Float
        get() = sqrt(x * x + y * y + z * z)

    // Нормализованный вектор (возвращает новый экземпляр)
    val normalized: Vector3
        get() = if (magnitude != 0f) Vector3(x / magnitude, y / magnitude, z / magnitude) else Zero

    // Нормализация текущего вектора (изменяет текущий экземпляр)
    fun normalize() {
        val mag = magnitude
        if (mag != 0f) {
            x /= mag
            y /= mag
            z /= mag
        }
    }

    companion object {
        val Zero = Vector3(0f, 0f, 0f)
        val One = Vector3(1f, 1f, 1f)
        val Forward = Vector3(0f, 0f, 1f)
        val Backward = Vector3(0f, 0f, -1f)
        val Up = Vector3(0f, 1f, 0f)
        val Down = Vector3(0f, -1f, 0f)
        val Left = Vector3(-1f, 0f, 0f)
        val Right = Vector3(1f, 0f, 0f)
    }
}