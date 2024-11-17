package com.gem.framework.utils

import kotlin.math.*

data class Quaternion(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var w: Float = 1f
) {

    // Нормализация кватерниона
    fun normalize() {
        val mag = magnitude()
        if (mag > 0f) {
            val invMag = 1f / mag
            x *= invMag
            y *= invMag
            z *= invMag
            w *= invMag
        }
    }

    fun normalized(): Quaternion {
        val q = this.copy()
        q.normalize()
        return q
    }

    // Длина кватерниона
    fun magnitude(): Float = sqrt(x * x + y * y + z * z + w * w)

    // Конъюгат кватерниона
    fun conjugate(): Quaternion = Quaternion(-x, -y, -z, w)

    // Инверсия кватерниона
    fun inverse(): Quaternion {
        val mag = magnitude()
        if (mag > 0f) {
            val invMagSq = 1f / (mag * mag)
            return conjugate().scalarMultiply(invMagSq)
        }
        throw IllegalStateException("Cannot invert a quaternion with zero magnitude")
    }

    // Умножение кватернионов
    operator fun times(other: Quaternion): Quaternion {
        return Quaternion(
            w * other.x + x * other.w + y * other.z - z * other.y,
            w * other.y - x * other.z + y * other.w + z * other.x,
            w * other.z + x * other.y - y * other.x + z * other.w,
            w * other.w - x * other.x - y * other.y - z * other.z
        )
    }

    // Умножение кватерниона на скаляр
    fun scalarMultiply(scalar: Float): Quaternion {
        return Quaternion(x * scalar, y * scalar, z * scalar, w * scalar)
    }

    // Преобразование в матрицу 4x4
    fun toMatrix(): Matrix4 {
        val xx = x * x
        val yy = y * y
        val zz = z * z
        val xy = x * y
        val xz = x * z
        val yz = y * z
        val wx = w * x
        val wy = w * y
        val wz = w * z

        return Matrix4(floatArrayOf(
            1f - 2f * (yy + zz), 2f * (xy - wz), 2f * (xz + wy), 0f,
            2f * (xy + wz), 1f - 2f * (xx + zz), 2f * (yz - wx), 0f,
            2f * (xz - wy), 2f * (yz + wx), 1f - 2f * (xx + yy), 0f,
            0f, 0f, 0f, 1f
        ))
    }

    // Преобразование в углы Эйлера
    fun toEulerAngles(): Vector3 {
        val sinRCosP = 2f * (w * x + y * z)
        val cosRCosP = 1f - 2f * (x * x + y * y)
        val roll = atan2(sinRCosP, cosRCosP)

        val sinP = 2f * (w * y - z * x)
        val pitch = if (abs(sinP) >= 1f) {
            if (sinP > 0) PI.toFloat() / 2f else -PI.toFloat() / 2f
        } else {
            asin(sinP)
        }

        val sinYCosP = 2f * (w * z + x * y)
        val cosYCosP = 1f - 2f * (y * y + z * z)
        val yaw = atan2(sinYCosP, cosYCosP)

        return Vector3(roll, pitch, yaw)
    }

    // Преобразование из углов Эйлера
    companion object {
        fun fromEulerAngles(eulerAngles: Vector3): Quaternion {
            val (roll, pitch, yaw) = eulerAngles

            val cy = cos(yaw * 0.5f)
            val sy = sin(yaw * 0.5f)
            val cp = cos(pitch * 0.5f)
            val sp = sin(pitch * 0.5f)
            val cr = cos(roll * 0.5f)
            val sr = sin(roll * 0.5f)

            return Quaternion(
                sr * cp * cy - cr * sp * sy,
                cr * sp * cy + sr * cp * sy,
                cr * cp * sy - sr * sp * cy,
                cr * cp * cy + sr * sp * sy
            ).normalized()
        }

        // Преобразование из угла и вектора
        fun fromAxisAngle(axis: Vector3, angle: Float): Quaternion {
            val halfAngle = angle / 2f
            val sinHalfAngle = sin(halfAngle)

            return Quaternion(
                axis.x * sinHalfAngle,
                axis.y * sinHalfAngle,
                axis.z * sinHalfAngle,
                cos(halfAngle)
            ).normalized()
        }
    }

    override fun toString(): String = "Quaternion(x=$x, y=$y, z=$z, w=$w)"
}